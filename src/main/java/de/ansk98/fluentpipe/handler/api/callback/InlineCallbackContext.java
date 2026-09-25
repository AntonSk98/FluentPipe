package de.ansk98.fluentpipe.handler.api.callback;

/**
 * Represents the context of a pressed inline keyboard button.
 * <p>
 * Inline buttons do not belong to an ongoing conversation, therefore they are dispatched
 * independently of the stateful {@link CallbackHandler} flow.
 *
 * @param ownerId         id of the user who pressed the button
 * @param channelId       id of the channel the button was pressed in
 * @param messageId       id of the message carrying the button
 * @param callbackQueryId id of the callback query, required to acknowledge the press
 * @param data            raw callback data of the pressed button
 * @author ansk98
 */
public record InlineCallbackContext(String ownerId, String channelId, Integer messageId, String callbackQueryId, String data) {
}
