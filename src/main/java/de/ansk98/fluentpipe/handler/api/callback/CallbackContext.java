package de.ansk98.fluentpipe.handler.api.callback;

/**
 * Represents the context for a callback handler.
 * See {@link CallbackHandler} for more details.
 *
 * @param ownerId   owner id
 * @param channelId channel id
 * @param input     input
 * @author ansk98
 */
public record CallbackContext(String ownerId, String channelId, String input) {
}
