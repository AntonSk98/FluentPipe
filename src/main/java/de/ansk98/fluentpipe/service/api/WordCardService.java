package de.ansk98.fluentpipe.service.api;

import de.ansk98.fluentpipe.service.api.commands.AddWordCommand;
import de.ansk98.fluentpipe.service.api.commands.DeleteWordCommand;
import de.ansk98.fluentpipe.service.api.commands.FetchActiveWordCardCommand;
import de.ansk98.fluentpipe.service.api.dto.WordCardDto;
import de.ansk98.fluentpipe.service.api.dto.WordDto;

/**
 * Service to manage word cards.
 *
 * @author ansk98
 */
public interface WordCardService {

    /**
     * Adds a new word to a word card. Words already present in the card are left unchanged.
     *
     * @param command command
     * @return the added or existing word
     */
    WordDto addWordToCard(AddWordCommand command);

    /**
     * Returns all words from a non-published word card.
     *
     * @param ownerId owner id
     * @return word card
     */
    WordCardDto fetchNotPublishedWordCard(String ownerId);

    /**
     * Returns the currently not published word card of a user.
     *
     * @param command command
     * @return the currently active word card
     */
    WordCardDto fetchActiveWordCard(FetchActiveWordCardCommand command);

    /**
     * Removes a word from a word card.
     *
     * @param command command
     * @return true if the word was found and removed, false otherwise
     */
    boolean removeWordFromCard(DeleteWordCommand command);

    /**
     * Publishes a word card.
     *
     * @param ownerId ownerId
     */
    void publishWordCard(String ownerId);

    /**
     * Clear not published word card.
     *
     * @param ownerId owner id
     */
    void clearNotPublishedWordCard(String ownerId);
}
