package de.ansk98.fluentpipe.domain;

/**
 * Represents the usage frequency classification of a word.
 *
 * @author ansk98
 */
public enum WordFrequency {

    VERY_RARE(1),
    RARE(2),
    MODERATE(3),
    OFTEN(4),
    VERY_OFTEN(5);

    private final int rank;

    /**
     * Constructor.
     *
     * @param rank rank
     */
    WordFrequency(int rank) {
        this.rank = rank;
    }

    /**
     * Returns the numeric ranking of the frequency level, where 1 is the lowest
     * ({@link #VERY_RARE}) and 5 is the highest ({@link #VERY_OFTEN}).
     *
     * @return the frequency ranking
     */
    public int getRank() {
        return rank;
    }
}