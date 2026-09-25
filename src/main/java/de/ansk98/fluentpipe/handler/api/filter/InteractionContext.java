package de.ansk98.fluentpipe.handler.api.filter;

import org.jspecify.annotations.Nullable;

/**
 * Encapsulates parameters and metadata associated with an incoming {@link InteractionFilter}.
 *
 * @author ansk98
 */
public record InteractionContext(@Nullable String userId, @Nullable String username) {

    public static final InteractionContext EMPTY_CONTEXT = new InteractionContext(null, null);

    /**
     * Creates a new instance of {@link InteractionContext}.
     *
     * @param userId   user identifier
     * @param username username
     * @return a new {@link InteractionContext} instance
     */
    public static InteractionContext of(String userId, String username) {
        return new InteractionContext(userId, username);
    }
}