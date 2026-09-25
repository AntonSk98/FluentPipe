package de.ansk98.fluentpipe.service.api;

import java.util.Optional;

/**
 * Manages active user conversation states, tracking ongoing commands
 * and resolving their corresponding callback mappings.
 *
 * @author ansk98
 */
public interface ConversationContextService {

    /**
     * Checks if the specified user has an ongoing command session.
     *
     * @param ownerId the unique identifier of the user
     * @return true if an active command exists, false otherwise
     */
    boolean hasActiveCommand(String ownerId);

    /**
     * Retrieves the active command string for the specified user.
     *
     * @param ownerId the unique identifier of the user
     * @return the active command string, or null if none exists
     */
    Optional<String> fetchActiveCommand(String ownerId);

    /**
     * Resolves the configured callback key associated with the user's ongoing command.
     *
     * @param ownerId the unique identifier of the user
     * @return the mapped callback string, or null if no active command or mapping exists
     */
    Optional<String> fetchCallbackForActiveCommand(String ownerId);

    /**
     * Marks a command as active for the user.
     *
     * @param ownerId the unique identifier of the user
     * @param command the command string to set as active
     */
    void startCommand(String ownerId, String command);

    /**
     * Clears any active command state for the specified user.
     *
     * @param ownerId the unique ownerId of the user
     */
    void clearCommand(String ownerId);
}