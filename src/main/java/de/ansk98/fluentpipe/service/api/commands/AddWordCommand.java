package de.ansk98.fluentpipe.service.api.commands;

import de.ansk98.fluentpipe.domain.WordFrequency;

/**
 * Command to add a new word to a word card.
 *
 * @param ownerId            owner id a word card belongs to
 * @param word               a word to be added to a word card
 * @param translation        the translation of the word
 * @param meaning            words meaning
 * @param frequency          the usage frequency level
 * @param example            an example sentence using the word
 * @param exampleTranslation the translation of the example sentence
 * @author ansk98
 */
public record AddWordCommand(String ownerId,
                             String word,
                             String translation,
                             String meaning,
                             WordFrequency frequency,
                             String example,
                             String exampleTranslation) {
}
