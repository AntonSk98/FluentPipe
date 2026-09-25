package de.ansk98.fluentpipe.handler.api.callback;

/**
 * Contract for handling a pressed inline keyboard button.
 * <p>
 * In contrast to a {@link CallbackHandler}, an inline callback is not bound to an ongoing
 * command. It is dispatched whenever a user presses one of the bot's inline buttons and is
 * matched by the prefix its callback data starts with.
 *
 * @author ansk98
 */
public interface InlineCallbackHandler {

    /**
     * Returns the callback data prefix this handler is responsible for.
     *
     * @return the supported callback data prefix
     */
    String supportedCallbackPrefix();

    /**
     * Executes the logic for the pressed inline button.
     *
     * @param context the inline callback context
     */
    void handle(InlineCallbackContext context);
}
