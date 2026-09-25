package de.ansk98.fluentpipe.domain;

import jakarta.persistence.*;

import java.util.UUID;

/**
 * Represents a word that is tracked for learning and vocabulary expansion.
 *
 * @author ansk98
 */
@Entity
public class Word {

    @Id
    private UUID id;

    /** The target word in the target language. */
    private String word;

    /** The translation of the word in the native language. */
    private String translation;

    /** The meaning/definition of the word in the native language. */
    private String meaning;

    /** The usage frequency level of the word. */
    @Enumerated(EnumType.STRING)
    private WordFrequency frequency;

    /** An example sentence using the word in the target language. */
    private String example;

    /** The translation of the example sentence in the native language. */
    private String exampleTranslation;

    /**
     * JPA no-arg constructor.
     */
    public Word() {
        this.id = UUID.randomUUID();
    }

    /**
     * Factory method to create a new word instance.
     *
     * @param word               the target word
     * @param translation        the translation of the word
     * @param meaning            words meaning
     * @param frequency          the usage frequency level
     * @param example            an example sentence using the word
     * @param exampleTranslation the translation of the example sentence
     * @return a new {@link Word} instance
     */
    public static Word newWord(String word,
                               String translation,
                               String meaning,
                               WordFrequency frequency,
                               String example,
                               String exampleTranslation) {
        Word wordEntity = new Word();
        wordEntity.word = word;
        wordEntity.translation = translation;
        wordEntity.meaning = meaning;
        wordEntity.frequency = frequency;
        wordEntity.example = example;
        wordEntity.exampleTranslation = exampleTranslation;
        return wordEntity;
    }

    /**
     * Returns the id of the word.
     *
     * @return the word id
     */
    public UUID getId() {
        return id;
    }

    /**
     * Returns the target word in the target language.
     *
     * @return the target word
     */
    public String getWord() {
        return word;
    }

    /**
     * Returns the translation of the word in the native language.
     *
     * @return the translation of the word
     */
    public String getTranslation() {
        return translation;
    }

    /**
     * Returns the meaning/definition of the word in the native language.
     *
     * @return the meaning of the word
     */
    public String getMeaning() {
        return meaning;
    }

    /**
     * Returns the usage frequency level of the word.
     *
     * @return the usage frequency level
     */
    public WordFrequency getFrequency() {
        return frequency;
    }

    /**
     * Returns an example sentence using the word in the target language.
     *
     * @return the example sentence
     */
    public String getExample() {
        return example;
    }

    /**
     * Returns the translation of the example sentence in the native language.
     *
     * @return the translation of the example sentence
     */
    public String getExampleTranslation() {
        return exampleTranslation;
    }
}