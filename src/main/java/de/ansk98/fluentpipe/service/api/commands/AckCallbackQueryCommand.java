package de.ansk98.fluentpipe.service.api.commands;

/**
 * Command to acknowledge a pressed inline keyboard button.
 * <p>
 * Telegram shows a small popup or a toast notification for the acknowledgement. It cannot
 * render audio, so the audio itself has to be sent as a separate message.
 *
 * @param callbackQueryId id of the callback query to acknowledge
 * @param text            text shown in the popup, may be null
 * @param showAlert       true to show a modal popup instead of a toast
 * @author ansk98
 */
public record AckCallbackQueryCommand(String callbackQueryId, String text, boolean showAlert) {
}
