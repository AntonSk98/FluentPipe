package de.ansk98.fluentpipe.service.api.commands;

/**
 * Command to delete a word from a word card.
 *
 * @param ownerId owner id a word card belongs to
 * @param word    the word to be removed from a word card
 * @author ansk98
 */
public record DeleteWordCommand(String ownerId, String word) {
}
