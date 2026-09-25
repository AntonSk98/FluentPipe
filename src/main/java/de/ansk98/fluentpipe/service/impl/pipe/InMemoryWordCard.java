package de.ansk98.fluentpipe.service.impl.pipe;

import java.util.Collection;

/**
 * In-memory document representation of a word card containing a collection of vocabulary items.
 *
 * @param words the collection of words included in this card document
 * @author ansk98
 */
public record InMemoryWordCard(Collection<InMemoryWord> words) {

    /**
     * In-memory representation of a single vocabulary entry within a word card document.
     *
     * @param word               the primary target word or phrase
     * @param translation        the translated meaning of the word
     * @param meaning            words meaning
     * @param frequency          the frequency categorization or usage level of the word
     * @param example            an example sentence demonstrating the word's usage in context
     * @param exampleTranslation the translation of the example sentence
     */
    public record InMemoryWord(String word,
                               String translation,
                               String meaning,
                               int frequency,
                               String example,
                               String exampleTranslation) {
    }
}