package de.ansk98.fluentpipe.handler.api.callback;

import de.ansk98.fluentpipe.service.api.ConversationContextService;

/**
 * Convenient abstract base class for callback handlers that automatically
 * transitions the conversation into a finished state after execution.
 *
 * @author ansk98
 */
public abstract class StatefulCallbackHandler implements CallbackHandler {

    private final ConversationContextService conversationContextService;

    /**
     * Constructor.
     *
     * @param conversationContextService See {@link ConversationContextService}
     */
    protected StatefulCallbackHandler(ConversationContextService conversationContextService) {
        this.conversationContextService = conversationContextService;
    }

    /**
     * Executes the core business logic for the incoming callback context.
     *
     * @param context the callback context
     */
    public abstract void handleStateful(CallbackContext context);

    @Override
    public void handle(CallbackContext context) {
        if (!conversationContextService.hasActiveCommand(context.ownerId())) {
            throw new IllegalStateException("User " + context.ownerId() + " has no ongoing command");
        }

        handleStateful(context);

        conversationContextService.clearCommand(context.ownerId());
    }
}
