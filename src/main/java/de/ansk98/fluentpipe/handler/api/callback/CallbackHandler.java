package de.ansk98.fluentpipe.handler.api.callback;

/**
 * Contract for handling the follow-up message of a stateful command.
 * <p>
 * Some commands cannot complete without extra input from the user. Such a command first prompts the
 * user for that input and, once an active state is recorded, the next regular message the user sends
 * is not treated as a new command. Instead, it is routed to the {@link CallbackHandler} that is bound to
 * the active command via the {@code commands} configuration, together with the user's reply.
 * <p>
 * Example: {@code /add_term} needs a word before it can add one. It therefore ends by asking for the
 * word; when the user replies, the message is dispatched to the callback bound to {@code /add_term},
 * which then analyzes and adds the word.
 *
 * @author ansk98
 */
public interface CallbackHandler {

    /**
     * Returns the callback key this handler listens for when a follow-up message is routed.
     *
     * @return the supported callback key
     */
    String supportedCallbackKey();

    /**
     * Executes the follow-up logic for the user's reply to a stateful command.
     *
     * @param context the callback context
     */
    void handle(CallbackContext context);

}