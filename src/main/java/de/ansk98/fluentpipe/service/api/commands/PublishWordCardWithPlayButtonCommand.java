package de.ansk98.fluentpipe.service.api.commands;

import java.util.List;

/**
 * Command to publish a word card to a channel as a single photo message with a caption,
 * carrying an inline play button that plays the spoken words on demand.
 *
 * @param channelId     channel id
 * @param caption       caption
 * @param audioToken    token the play button resolves the audio with
 * @param imageBinaries images representing the word card
 * @author ansk98
 */
public record PublishWordCardWithPlayButtonCommand(String channelId, String caption, String audioToken, List<byte[]> imageBinaries) {
}
