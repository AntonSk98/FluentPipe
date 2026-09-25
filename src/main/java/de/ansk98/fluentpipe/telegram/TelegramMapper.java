package de.ansk98.fluentpipe.telegram;

import de.ansk98.fluentpipe.handler.api.callback.CallbackContext;
import de.ansk98.fluentpipe.handler.api.command.Command;
import de.ansk98.fluentpipe.handler.api.filter.InteractionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

import static de.ansk98.fluentpipe.handler.api.filter.InteractionContext.EMPTY_CONTEXT;

/**
 * Mapper responsible for converting raw Telegram {@link Update} objects
 * into internal domain models and contexts.
 *
 * @author ansk98
 */
@Component
public class TelegramMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramMapper.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Extracts user and chat metadata from a Telegram update to construct an {@link InteractionContext}.
     *
     * @param update the raw incoming Telegram update
     * @return the mapped {@link InteractionContext} containing the user ID and username,
     * or {@link InteractionContext#EMPTY_CONTEXT} if the update contains no valid message or sender
     */
    public InteractionContext toInteractionContext(Update update) {
        return Optional.ofNullable(update.getMessage())
                .map(Message::getFrom)
                .map(user -> InteractionContext.of(String.valueOf(user.getId()), user.getUserName()))
                .orElse(EMPTY_CONTEXT);
    }

    /**
     * Extracts and builds a {@link Command} representation from a Telegram update.
     *
     * @param update the raw incoming Telegram update
     * @return the mapped {@link Command}, or {@code null} if no command is present
     */
    public Command toCommand(Update update) {
        return new Command(TelegramUtils.userId(update), TelegramUtils.chatId(update));
    }

    /**
     * Extracts callback metadata from an update to construct a {@link CallbackContext}.
     *
     * @param update the raw incoming Telegram update containing a callback query
     * @return the mapped {@link CallbackContext}, or {@code null} if no callback data is present
     */
    public CallbackContext toCallbackContext(Update update) {
        return Optional.ofNullable(update.getMessage())
                .map(message -> new CallbackContext(TelegramUtils.userId(update), TelegramUtils.chatId(update), message.getText()))
                .orElseThrow(() -> {
                    String payload = OBJECT_MAPPER.writeValueAsString(update);
                    LOGGER.error("Cannot extract callback context from {}", payload);
                    return new IllegalStateException("Cannot extract callback context from " + payload);
                });
    }
}