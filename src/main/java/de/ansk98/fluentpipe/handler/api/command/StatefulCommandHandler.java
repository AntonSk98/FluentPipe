package de.ansk98.fluentpipe.handler.api.command;

import de.ansk98.fluentpipe.service.api.ConversationContextService;

/**
 * Convenient abstract base class for command handlers that automatically
 * transitions the conversation into an ongoing state after execution.
 *
 * @author ansk98
 */
public abstract class StatefulCommandHandler implements CommandHandler {

    private final ConversationContextService conversationContextService;

    /**
     * Constructor.
     *
     * @param conversationContextService See {@link ConversationContextService}
     */
    protected StatefulCommandHandler(ConversationContextService conversationContextService) {
        this.conversationContextService = conversationContextService;
    }

    /**
     * Executes the core business logic for the command.
     *
     * @param command command
     */
    public abstract void handleStateful(Command command);

    @Override
    public void handle(Command command) {
        handleStateful(command);

        conversationContextService.startCommand(command.ownerId(), supportedCommand());
    }
}
