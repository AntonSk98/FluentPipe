package de.ansk98.fluentpipe.service.api.commands;

/**
 * Command to register a new RSS feed subscription.
 *
 * @param subscriptionId id of the subscription
 * @param rssUrl         url of the RSS feed to subscribe to
 * @author ansk98
 */
public record RegisterFeedCommand(String subscriptionId, String rssUrl) {
}
