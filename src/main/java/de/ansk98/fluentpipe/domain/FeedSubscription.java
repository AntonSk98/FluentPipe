package de.ansk98.fluentpipe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Entity representing a dynamically managed RSS/Atom feed subscription configuration.
 *
 * <p>Maintains the baseline configuration metadata needed to rebuild and restart
 * dynamic integration flows across application context reboots.</p>
 *
 * @author ansk98
 */
@Entity
@Table(name = "feed_subscription")
public class FeedSubscription {

    @Id
    @Column(name = "subscription_id", nullable = false, length = 255)
    private String subscriptionId;

    @Column(name = "rss_url", nullable = false, length = 2048)
    private String rssUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private final Instant createdAt = Instant.now();

    /**
     * Default no-argument constructor required by JPA specifications.
     */
    public FeedSubscription() {
    }

    /**
     * Fully parameterized constructor to quickly instantiate a configuration state.
     *
     * @param subscriptionId the unique identifier for this dynamic pipeline
     * @param rssUrl         the target RSS resource endpoint location
     */
    public FeedSubscription(String subscriptionId, String rssUrl) {
        this.subscriptionId = subscriptionId;
        this.rssUrl = rssUrl;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public String getRssUrl() {
        return rssUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
