package de.ansk98.fluentpipe.service.api.commands;

/**
 * Command to unregister an existing RSS feed subscription.
 *
 * @param subscriptionId id of the subscription to remove
 * @author ansk98
 */
public record UnregisterFeedCommand(String subscriptionId) {
}
