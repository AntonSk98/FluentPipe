package de.ansk98.fluentpipe.service.impl;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import de.ansk98.fluentpipe.config.NewsSourceProperties;
import de.ansk98.fluentpipe.service.api.commands.SendNewsFeedCommand;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.feed.dsl.Feed;
import org.springframework.integration.metadata.ConcurrentMetadataStore;
import org.springframework.integration.metadata.MetadataStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Initializes and registers dynamic RSS feed integration pipelines on startup.
 *
 * @author ansk98
 */
@Service
public class RssFeedBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(RssFeedBootstrap.class);

    private final NewsSourceProperties newsSourceProperties;
    private final IntegrationFlowContext flowContext;
    private final MetadataStore metadataStore;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Constructor.
     *
     * @param newsSourceProperties See {@link NewsSourceProperties}
     * @param flowContext          See {@link IntegrationFlowContext}
     * @param metadataStore        See {@link ConcurrentMetadataStore}
     * @param eventPublisher       See {@link ApplicationEventPublisher}
     */
    public RssFeedBootstrap(NewsSourceProperties newsSourceProperties,
                            IntegrationFlowContext flowContext,
                            MetadataStore metadataStore,
                            ApplicationEventPublisher eventPublisher) {
        this.newsSourceProperties = newsSourceProperties;
        this.flowContext = flowContext;
        this.metadataStore = metadataStore;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Automatically hooks into the context startup event to reload and boot active
     * feed pipelines that were preserved in persistent repository storage.
     */
    @EventListener(ApplicationReadyEvent.class)
    @Transactional(readOnly = true)
    void subscribeFeedsOnStartup() {
        newsSourceProperties.sources().forEach(feedSource -> {
            if (metadataStore.get(feedSource.feedId()) == null) {
                metadataStore.put(feedSource.feedId(), String.valueOf(Instant.now().minus(30, ChronoUnit.MINUTES).toEpochMilli()));
            }

            this.bootSubscriptionFlow(feedSource);
        });
    }

    private void bootSubscriptionFlow(NewsSourceProperties.Source feedSource) {
        IntegrationFlow flow = IntegrationFlow
                .from(
                        Feed.inboundAdapter(feedSource.feedUrlAsURL(), feedSource.feedId()).metadataStore(metadataStore),
                        e -> e.poller(Pollers.fixedDelay(Duration.ofMinutes(10)).transactional())
                )
                .<SyndEntry>handle((payload, _) -> {
                    var sendNewFeedCommand = new SendNewsFeedCommand(
                            newsSourceProperties.receiverId(),
                            payload.getTitle(),
                            Optional.ofNullable(payload.getDescription()).map(SyndContent::getValue).orElse(Strings.EMPTY),
                            payload.getLink(),
                            feedSource.sourceTag()
                    );

                    eventPublisher.publishEvent(sendNewFeedCommand);
                    return null;
                })
                .get();

        this.flowContext.registration(flow)
                .id(feedSource.feedId())
                .register();
    }
}
