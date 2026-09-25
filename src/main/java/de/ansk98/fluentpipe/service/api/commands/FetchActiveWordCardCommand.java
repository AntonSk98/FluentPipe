package de.ansk98.fluentpipe.service.api.commands;

/**
 * Command to fetch the currently not published word card of a user.
 *
 * @param ownerId owner id
 * @author ansk98
 */
public record FetchActiveWordCardCommand(String ownerId) {
}