-- Migration Script: Creates the feed subscription table for dynamic configurations.
-- Target Database: MySQL
CREATE TABLE feed_subscription
(
    subscription_id VARCHAR(255)  NOT NULL,
    rss_url         VARCHAR(2048) NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (subscription_id)
);
