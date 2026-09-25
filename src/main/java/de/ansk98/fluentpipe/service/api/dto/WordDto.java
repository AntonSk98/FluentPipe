package de.ansk98.fluentpipe.service.api.dto;

import de.ansk98.fluentpipe.domain.Word;
import de.ansk98.fluentpipe.domain.WordFrequency;

/**
 * DTO holding the properties of a single word.
 *
 * @author ansk98
 */
public record WordDto(
        String word,
        String translation,
        String meaning,
        WordFrequency frequency,
        String example,
        String exampleTranslation
) {

    /**
     * Maps a {@link Word} entity to a {@link WordDto}.
     *
     * @param word the word entity
     * @return the word DTO
     */
    public static WordDto from(Word word) {
        return new WordDto(
                word.getWord(),
                word.getTranslation(),
                word.getMeaning(),
                word.getFrequency(),
                word.getExample(),
                word.getExampleTranslation()
        );
    }
}