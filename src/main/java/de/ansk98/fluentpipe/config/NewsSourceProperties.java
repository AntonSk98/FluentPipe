package de.ansk98.fluentpipe.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

/**
 * Properties to define RSS news sources.
 *
 * @param receiverId identifier that should receive publishing news
 * @param sources    sources
 * @author ansk98
 */
@ConfigurationProperties(prefix = "news")
public record NewsSourceProperties(String receiverId, List<Source> sources) {

    /**
     * Represents an individual RSS news source configuration.
     *
     * @param feedId    the unique identifier for the target destination channel or feed
     * @param feedUrl   the absolute URL of the RSS feed
     * @param sourceTag the label or tag identifying the origin of the news source
     * @author ansk98
     */
    public record Source(
            String feedId,
            String feedUrl,
            String sourceTag
    ) {
        public URL feedUrlAsURL() {
            try {
                return new URI(feedUrl).toURL();
            } catch (MalformedURLException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
