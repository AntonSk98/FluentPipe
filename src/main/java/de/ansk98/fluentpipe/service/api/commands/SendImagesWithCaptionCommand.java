package de.ansk98.fluentpipe.service.api.commands;

import java.util.List;

/**
 * Command to send images with a caption
 *
 * @param channelId     channel id
 * @param caption       caption
 * @param imageBinaries images represented as binaries
 * @author ansk98
 */
public record SendImagesWithCaptionCommand(String channelId, String caption, List<byte[]> imageBinaries) {
}
