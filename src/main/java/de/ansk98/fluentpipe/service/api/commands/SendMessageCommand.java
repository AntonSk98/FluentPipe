package de.ansk98.fluentpipe.service.api.commands;

/**
 * Command to send a message to a user with {@code ownerId}.
 *
 * @param channelId channel id
 * @param message   message
 * @param payload   optional object serialized as a JSON payload and appended to the message
 * @author ansk98
 */
public record SendMessageCommand(String channelId, String message, Object payload) {

    /**
     * Creates a plain text message command without a JSON payload.
     *
     * @param chatId  chat id
     * @param message message
     */
    public SendMessageCommand(String chatId, String message) {
        this(chatId, message, null);
    }
}
