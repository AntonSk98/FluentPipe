package de.ansk98.fluentpipe.service.impl.pipe;

import java.util.List;

/**
 * Represents a word card split in images.
 *
 * @param images word card as images
 * @author ansk98
 */
public record WordCardImages(List<byte[]> images) {
}
