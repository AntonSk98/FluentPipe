package de.ansk98.fluentpipe.domain;

import jakarta.persistence.*;

import java.util.*;

/**
 * Represents a collection or card grouping related vocabulary words together.
 *
 * @author ansk98
 */
@Entity
public class WordCard {

    @Id
    private final UUID id;

    @Column(nullable = false)
    private String ownerId;

    @JoinColumn(name = "word_card_id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Word> words = new ArrayList<>();

    private boolean published;

    /**
     * JPA no-arg constructor.
     */
    public WordCard() {
        this.id = UUID.randomUUID();
    }

    /**
     * Factory method to create a new word card instance.
     *
     * @return a new {@link WordCard} instance
     */
    public static WordCard newCard(String ownerId) {
        WordCard card = new WordCard();
        card.ownerId = ownerId;
        card.published = false;
        return card;
    }

    /**
     * Returns the id of the word card.
     *
     * @return word card id
     */
    public UUID getId() {
        return id;
    }

    /**
     * Publishes a {@link WordCard}
     */
    public void publishCard() {
        this.published = true;
    }

    /**
     * Adds a word to this card and maintains the bidirectional-like ownership link.
     *
     * @param word the word to add
     */
    public void addWord(Word word) {
        this.words.add(word);
    }

    /**
     * Removes a word from this card, triggering orphan removal.
     *
     * @param word the word to remove
     */
    public void removeWord(Word word) {
        this.words.remove(word);
    }

    /**
     * Returns the word matching the given word text, if present in this card.
     *
     * @param word the word text to look up
     * @return the matching word, or {@link Optional#empty()} if absent
     */
    public Optional<Word> findWord(String word) {
        return this.words.stream()
                .filter(existing -> existing.getWord().equalsIgnoreCase(word))
                .findFirst();
    }

    /**
     * Returns words in a word card.
     *
     * @return words in a word card
     */
    public List<Word> getWords() {
        return Collections.unmodifiableList(this.words);
    }
}