package de.ansk98.fluentpipe.service.impl;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import de.ansk98.fluentpipe.config.NewsSourceProperties;
import de.ansk98.fluentpipe.service.api.commands.SendNewsFeedCommand;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.feed.dsl.Feed;
import org.springframework.integration.metadata.SimpleMetadataStore;
import org.springframework.stereotype.Service;

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
@ConditionalOnProperty(prefix = "fluentpipe.news", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RssFeedBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(RssFeedBootstrap.class);

    private final NewsSourceProperties newsSourceProperties;
    private final IntegrationFlowContext flowContext;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Constructor.
     *
     * @param newsSourceProperties See {@link NewsSourceProperties}
     * @param flowContext          See {@link IntegrationFlowContext}
     * @param eventPublisher       See {@link ApplicationEventPublisher}
     */
    public RssFeedBootstrap(NewsSourceProperties newsSourceProperties,
                            IntegrationFlowContext flowContext,
                            ApplicationEventPublisher eventPublisher) {
        this.newsSourceProperties = newsSourceProperties;
        this.flowContext = flowContext;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Starts configured feed pipelines with a five-minute lookback and in-memory history.
     */
    @EventListener(ApplicationReadyEvent.class)
    void subscribeFeedsOnStartup() {
        newsSourceProperties.sources().forEach(this::bootSubscriptionFlow);
    }

    private void bootSubscriptionFlow(NewsSourceProperties.Source feedSource) {
        Instant firstPublishSince = Instant.now().minus(5, ChronoUnit.MINUTES);
        LOGGER.info("Starting feed '{}' ({}): only items published after {} will be posted.",
                feedSource.feedId(), feedSource.feedUrl(), firstPublishSince);
        var metadataStore = new SimpleMetadataStore();
        metadataStore.put(feedSource.feedId(), String.valueOf(firstPublishSince.toEpochMilli()));
        IntegrationFlow flow = IntegrationFlow
                .from(
                        Feed.inboundAdapter(feedSource.feedUrlAsURL(), feedSource.feedId()).metadataStore(metadataStore),
                        e -> e.poller(Pollers.fixedDelay(Duration.ofMinutes(10)))
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
