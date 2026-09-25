package de.ansk98.fluentpipe.handler.api.command;

/**
 * Encapsulates the details of a command.
 * See {@link CommandHandler} for more details.
 *
 * @param ownerId   owner id
 * @param channelId channel id
 * @author ansk98
 */
public record Command(String ownerId, String channelId) {
}
