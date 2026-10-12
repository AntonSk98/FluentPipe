package de.ansk98.fluentpipe.service.impl;

import com.rometools.rome.feed.synd.SyndEntry;
import de.ansk98.fluentpipe.model.FeedSubscription;
import de.ansk98.fluentpipe.repository.FeedSubscriptionRepository;
import de.ansk98.fluentpipe.service.api.DynamicFeedService;
import de.ansk98.fluentpipe.service.api.commands.RegisterFeedCommand;
import de.ansk98.fluentpipe.service.api.commands.UnregisterFeedCommand;
import de.ansk98.fluentpipe.service.api.dto.RegisteredFeedDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.feed.dsl.Feed;
import org.springframework.integration.metadata.ConcurrentMetadataStore;
import org.springframework.integration.metadata.MetadataStore;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.List;

/**
 * Implementation of {@link DynamicFeedService}.
 *
 * @author ansk98
 */
@Service
public class DynamicFeedServiceImpl implements DynamicFeedService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicFeedServiceImpl.class);

    private final IntegrationFlowContext flowContext;
    private final MetadataStore metadataStore;
    private final FeedSubscriptionRepository subscriptionRepository;

    /**
     * Constructor.
     *
     * @param flowContext            See {@link IntegrationFlowContext}
     * @param metadataStore          See {@link ConcurrentMetadataStore}
     * @param subscriptionRepository See {@link FeedSubscriptionRepository}
     */
    public DynamicFeedServiceImpl(IntegrationFlowContext flowContext,
                                  MetadataStore metadataStore,
                                  FeedSubscriptionRepository subscriptionRepository) {
        this.flowContext = flowContext;
        this.metadataStore = metadataStore;
        this.subscriptionRepository = subscriptionRepository;
    }

    /**
     * Automatically hooks into the context startup event to reload and boot active
     * feed pipelines that were preserved in persistent repository storage.
     */
    @EventListener(ApplicationReadyEvent.class)
    @Transactional(readOnly = true)
    public void reloadSubscriptionsOnStartup() {
        List<FeedSubscription> persistedFeeds = subscriptionRepository.findAll();
        LOGGER.info("Found {} feed subscription(s) in database to restore.", persistedFeeds.size());

        for (FeedSubscription subscription : persistedFeeds) {
            try {
                // Call the registration flow helper internal logic
                this.bootIntegrationFlow(subscription.getSubscriptionId(), subscription.getRssUrl());
                LOGGER.info("Successfully restored subscription stream on boot: {}", subscription.getSubscriptionId());
            } catch (Exception e) {
                LOGGER.error("Failed to restore feed subscription stream for ID [{}] on boot sequence",
                        subscription.getSubscriptionId(), e);
            }
        }
    }

    @Override
    @Transactional
    public void registerFeed(RegisterFeedCommand command) {
        try {
            // 1. Build and boot the running background Integration Pipeline engine
            this.bootIntegrationFlow(command.subscriptionId(), command.rssUrl());

            // 2. Persist the configuration properties to DB so it survives application reboots
            FeedSubscription subscription = new FeedSubscription(command.subscriptionId(), command.rssUrl());
            subscriptionRepository.save(subscription);

            LOGGER.info("Successfully subscribed to feed: {}", command.subscriptionId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to register dynamic feed", e);
        }
    }

    @Override
    public List<RegisteredFeedDto> findAllActiveFeeds() {
        return this.flowContext
                .getRegistry()
                .keySet()
                .stream()
                .map(RegisteredFeedDto::new)
                .toList();
    }

    @Override
    @Transactional
    public void unregisterFeed(UnregisterFeedCommand command) {
        String subscriptionId = command.subscriptionId();
        var registration = this.flowContext.getRegistrationById(subscriptionId);

        // 1. Check if the flow is actively running in memory registry
        if (registration != null) {

            // 2. Stop the polling thread gracefully before memory deletion to avoid race conditions
            registration.stop();

            // 3. Demolish orchestration engine routes from runtime memory context
            this.flowContext.remove(subscriptionId);

            // 4. Wipe config footprint out from DB table so it does not auto-boot on next start
            subscriptionRepository.deleteById(subscriptionId);

            LOGGER.info("Successfully unsubscribed and unregistered feed: {}", subscriptionId);
        } else {
            LOGGER.warn("Attempted to unregister feed '{}', but no active subscription was found.", subscriptionId);
        }
    }

    @ServiceActivator(inputChannel = "feedErrorChannel")
    public void handleFeedErrors(MessagingException exception) {
        // Safe context checking: Avoid trace pollution if thread finishes during an unregistration step
        if (exception.getCause() == null && exception.getFailedMessage() == null) {
            return;
        }

        String errorMessage;
        if (exception.getCause() != null) {
            errorMessage = exception.getCause().getMessage();
        } else if (exception.getFailedMessage() != null) {
            errorMessage = exception.getFailedMessage().toString();
        } else {
            errorMessage = exception.getMessage();
        }

        LOGGER.error("Feed poll failed temporarily: {}", errorMessage);
    }

    /**
     * Shared internal helper strategy to construct and register the running pipeline.
     */
    private void bootIntegrationFlow(String subscriptionId, String rssUrl) throws Exception {
        URL feedUrl = new URI(rssUrl).toURL();

        IntegrationFlow flow = IntegrationFlow
                .from(
                        Feed.inboundAdapter(feedUrl, subscriptionId).metadataStore(metadataStore),
                        e -> e.poller(Pollers.fixedDelay(Duration.ofSeconds(5))
                                .transactional()
                                .errorChannel("feedErrorChannel")))
                .<SyndEntry>handle((payload, headers) -> {
                    LOGGER.info("{}:{}:{}:{}", payload.getTitle(), payload.getDescription(), payload.getLink(), payload.getPublishedDate());
                    return null;
                })
                .get();

        this.flowContext.registration(flow)
                .id(subscriptionId)
                .register();
    }
}
