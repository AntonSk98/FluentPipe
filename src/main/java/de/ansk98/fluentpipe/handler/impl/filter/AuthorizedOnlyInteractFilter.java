package de.ansk98.fluentpipe.handler.impl.filter;

import de.ansk98.fluentpipe.handler.api.filter.InteractionContext;
import de.ansk98.fluentpipe.handler.api.filter.InteractionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * Filter that restricts incoming interaction handling to the configured owner.
 *
 * @author ansk98
 */
@Component
public class AuthorizedOnlyInteractFilter implements InteractionFilter {

    private final static Logger LOGGER = LoggerFactory.getLogger(AuthorizedOnlyInteractFilter.class);

    /**
     * The owner identifier of the sole authorized owner.
     */
    @Value("${telegram.owner-id}")
    private String allowedOwnerId;

    /**
     * Determines whether the incoming interaction originates from the authorized owner.
     *
     * @param interactionContext the context containing details of the incoming interaction
     * @return {@code true} if the interaction's identifier matches the allowed owner ID, {@code false} otherwise
     */
    @Override
    public boolean isAllowedWithin(InteractionContext interactionContext) {
        boolean isAuthorizedToInteract = allowedOwnerId.equals(interactionContext.userId());
        if (!isAuthorizedToInteract) {
            LOGGER.info("Unauthorized attempt rejected to access the bot by {}.", interactionContext.username());
        }
        return isAuthorizedToInteract;
    }
}
