package de.ansk98.fluentpipe.repository;

import de.ansk98.fluentpipe.domain.ConversationState;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory store to keep track of ongoing commands for users.
 * Some commands require interaction with a user—for example, adding a new word requires first prompting the user to provide it.
 * Once provided, the input must be matched to the active command context.
 *
 * @author ansk98
 */
@Repository
public class ConversationStateStore {

    private final Map<String, String> ownerIdToActiveCommandMap = new ConcurrentHashMap<>();

    /**
     * Marks a command as ongoing.
     *
     * @param conversationState conversation state
     */
    public void markAsOngoingCommand(ConversationState conversationState) {
        ownerIdToActiveCommandMap.put(conversationState.ownerId(), conversationState.command());
    }

    /**
     * Helpful method to check if there is an active command for a user.
     *
     * @param ownerId owner id
     * @return true if there exists an ongoing command
     */
    public boolean hasActiveCommand(String ownerId) {
        return ownerIdToActiveCommandMap.containsKey(ownerId);
    }

    /**
     * Returns the active command for a user.
     *
     * @param ownerId owner id
     * @return active command
     * @throws IllegalStateException if there is no active command for a given user
     */
    public String requireActiveCommand(String ownerId) {
        if (hasActiveCommand(ownerId)) {
            return ownerIdToActiveCommandMap.get(ownerId);
        }

        throw new IllegalStateException("No active command for a user " + ownerId);
    }

    /**
     * Marks a command as finished.
     *
     * @param ownerId owner id
     */
    public void finishCommand(String ownerId) {
        ownerIdToActiveCommandMap.remove(ownerId);
    }
}
