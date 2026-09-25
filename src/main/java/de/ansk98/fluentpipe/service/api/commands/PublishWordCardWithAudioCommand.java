package de.ansk98.fluentpipe.service.api.commands;

import java.util.List;

/**
 * Command to publish a word card to a channel as images (preview) plus audio of the spoken words.
 *
 * @param channelId     channel id
 * @param caption       caption
 * @param audio         audio binary of the spoken words
 * @param imageBinaries images representing the word card
 * @author ansk98
 */
public record PublishWordCardWithAudioCommand(String channelId, String caption, byte[] audio, List<byte[]> imageBinaries) {
}