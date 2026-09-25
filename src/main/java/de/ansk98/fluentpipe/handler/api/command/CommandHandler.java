package de.ansk98.fluentpipe.handler.api.command;

/**
 * Contract for handling a user-issued bot command.
 * <p>
 * A {@link CommandHandler} is bound to a single command string returned by {@link #supportedCommand()}.
 * When the user sends a message that starts with that command, the bot routes the update to the
 * matching handler, which then executes the command's logic.
 * <p>
 * A command is either one-shot or stateful. A one-shot command (e.g. {@code /active_word_card})
 * completes entirely within {@link #handle(Command)}. A stateful command (see
 * {@link de.ansk98.fluentpipe.handler.api.command.StatefulCommandHandler}, e.g. {@code /add_term})
 * instead asks the user for additional input, records an active state, and is finished later by the
 * matching {@link de.ansk98.fluentpipe.handler.api.callback.CallbackHandler} once the user replies.
 *
 * @author ansk98
 */
public interface CommandHandler {

    /**
     * Returns the command string this handler is bound to.
     *
     * @return the supported command string (e.g., "/active_word_card")
     */
    String supportedCommand();

    /**
     * Executes the logic for an incoming command.
     *
     * @param command the command
     */
    void handle(Command command);

}
