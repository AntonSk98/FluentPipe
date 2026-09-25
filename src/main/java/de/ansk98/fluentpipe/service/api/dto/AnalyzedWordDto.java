package de.ansk98.fluentpipe.service.api.dto;

import de.ansk98.fluentpipe.domain.WordFrequency;

/**
 * Structured AI response for an analyzed word.
 *
 * @param word               the base/dictionary form of the word in the target language
 * @param translation        the translation of the word
 * @param meaning            words meaning
 * @param frequency          the usage frequency level
 * @param example            an example sentence using the word
 * @param exampleTranslation the translation of the example sentence
 * @author ansk98
 */
public record AnalyzedWordDto(
        String word,
        String translation,
        String meaning,
        WordFrequency frequency,
        String example,
        String exampleTranslation
) {
}