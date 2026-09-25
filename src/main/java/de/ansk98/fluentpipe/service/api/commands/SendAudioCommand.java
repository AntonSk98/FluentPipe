package de.ansk98.fluentpipe.service.api.commands;

/**
 * Command to send an audio message, optionally as a reply to an already sent message.
 *
 * @param channelId         channel id
 * @param audio             audio binary
 * @param replyToMessageId  id of the message to reply to, may be null
 * @author ansk98
 */
public record SendAudioCommand(String channelId, byte[] audio, Integer replyToMessageId) {
}
