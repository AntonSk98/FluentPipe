package de.ansk98.fluentpipe.handler.api.filter;

/**
 * Defines a filter to intercept and evaluate user interactions—such as
 * commands, text messages, or callbacks—to determine if they are
 * allowed to proceed through the pipeline.
 *
 * @author ansk98
 */
public interface InteractionFilter {

    /**
     * Checks whether the incoming interaction parameters meet the criteria
     * to be processed.
     *
     * @param interactionContext the parameters containing user, context, and input details
     * @return true if the interaction is allowed, false otherwise
     */
    boolean isAllowedWithin(InteractionContext interactionContext);
}
