package de.ansk98.fluentpipe.service.api.commands;

/**
 * Command to send news feed.
 *
 * @param channelId   channel id
 * @param title       title
 * @param description description
 * @param feedUrl     feed url to read the full article
 * @param sourceTag   source
 * @author ansk98
 */
public record SendNewsFeedCommand(String channelId,
                                  String title,
                                  String description,
                                  String feedUrl,
                                  String sourceTag) {
}
