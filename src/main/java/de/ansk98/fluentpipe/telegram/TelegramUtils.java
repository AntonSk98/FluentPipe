package de.ansk98.fluentpipe.telegram;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.Optional;

/**
 * Utility methods for inspecting and processing Telegram {@link Update} objects.
 *
 * @author ansk98
 */
public final class TelegramUtils {

    private TelegramUtils() {
        // Utility class
    }

    /**
     * Checks whether the given update contains a valid text message representing a command.
     *
     * @param update the Telegram update to check
     * @return true if the update has a message and it is a command, false otherwise
     */
    public static boolean isCommand(Update update) {
        if (update == null || !update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }
        return update.getMessage().isCommand();
    }

    /**
     * Extracts the command string from the update, ignoring any trailing arguments.
     *
     * @param update the Telegram update
     * @return the base command string (e.g., "/new-word"), or null if not present
     */
    public static String extractCommandString(Update update) {
        if (!isCommand(update)) {
            throw new IllegalStateException("Update contains no command");
        }

        return update.getMessage().getCommand();
    }

    /**
     * Extracts ownerId from the update.
     * <p>
     * Works for message updates as well as for callback queries triggered by inline buttons.
     *
     * @param update the Telegram update
     * @return owner id
     */
    public static String userId(Update update) {
        return Optional.ofNullable(update.getMessage())
                .map(Message::getFrom)
                .map(User::getId)
                .map(String::valueOf)
                .orElseThrow();
    }

    /**
     * Extracts chatId from the update.
     * <p>
     * Works for message updates as well as for callback queries triggered by inline buttons.
     *
     * @param update the Telegram update
     * @return chat id
     */
    public static String chatId(Update update) {
        return Optional.ofNullable(update.getMessage())
                .map(Message::getChat)
                .map(Chat::getId)
                .map(String::valueOf)
                .orElseThrow();
    }
}
