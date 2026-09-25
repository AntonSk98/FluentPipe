package de.ansk98.fluentpipe.domain;

/**
 * Represents the state of a stateful command.
 *
 * @author ansk98
 */
public record ConversationState(String ownerId, String command) {

    /**
     * Helper method to create a new {@link ConversationState}.
     *
     * @param ownerId ownerId
     * @param command command
     * @return new {@link ConversationState}
     */
    public static ConversationState create(String ownerId, String command) {
        return new ConversationState(ownerId, command);
    }
}
