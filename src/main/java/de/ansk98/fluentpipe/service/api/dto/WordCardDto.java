package de.ansk98.fluentpipe.service.api.dto;

import de.ansk98.fluentpipe.domain.Word;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO holding a collection of words within a word card.
 *
 * @author ansk98
 */
public record WordCardDto(Collection<WordDto> words) {

    /**
     * Maps a collection of {@link Word} entities to a {@link WordCardDto}.
     *
     * @param words the collection of word entities
     * @return the word card DTO
     */
    public static WordCardDto from(Collection<Word> words) {
        List<WordDto> wordDtos = words.stream()
                .map(WordDto::from)
                .toList();
        return new WordCardDto(wordDtos);
    }

    /**
     * Renders the word card as one {@code word - translation} row per line.
     *
     * @return the rendered word card
     */
    public String prettyPrint() {
        return words.stream()
                .map(word -> word.word() + " --> " + word.translation())
                .collect(Collectors.joining("\n"));
    }
}