package de.ansk98.fluentpipe.telegram;

import de.ansk98.fluentpipe.handler.api.callback.CallbackHandler;
import de.ansk98.fluentpipe.handler.api.command.CommandHandler;
import de.ansk98.fluentpipe.handler.api.filter.InteractionFilter;
import de.ansk98.fluentpipe.service.api.ConversationContextService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.DefaultLongPollingUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Core Telegram bot implementation for the FluentPipe framework.
 * Consumes incoming updates, enforces interaction filters, routes commands to their respective handlers,
 * and manages stateful conversation text-to-callback flows.
 *
 * @author ansk98
 */
@Component
class FluentPipeBot extends DefaultLongPollingUpdateConsumer {


    private final TelegramMapper telegramMapper;
    private final Map<String, CommandHandler> commandHandlers;
    private final Map<String, CallbackHandler> callbackHandlers;
    private final Collection<InteractionFilter> interactionFilters;
    private final ConversationContextService conversationContextService;


    /**
     * Constructs a new {@link FluentPipeBot} with the required mappers, services,
     * and registered handlers.
     *
     * @param telegramMapper             mapper for converting raw Telegram updates into domain contexts
     * @param conversationContextService service for tracking active user conversation states
     * @param callbackHandlers           collection of registered callback handlers
     * @param interactionFilters         collection of filters to evaluate incoming interaction permissions
     * @param commandHandlers            collection of registered command handlers
     */
    FluentPipeBot(
            TelegramMapper telegramMapper,
            ConversationContextService conversationContextService,
            Collection<CallbackHandler> callbackHandlers,
            Collection<InteractionFilter> interactionFilters,
            Collection<CommandHandler> commandHandlers) {
        this.telegramMapper = telegramMapper;
        this.conversationContextService = conversationContextService;

        this.interactionFilters = interactionFilters;

        this.commandHandlers = commandHandlers
                .stream()
                .collect(Collectors.toMap(CommandHandler::supportedCommand, Function.identity()));

        this.callbackHandlers = callbackHandlers
                .stream()
                .collect(Collectors.toMap(CallbackHandler::supportedCallbackKey, Function.identity()));
    }

    @Override
    public void consume(Update update) {
        var interactionContext = telegramMapper.toInteractionContext(update);

        boolean isFurtherInteractionNotAllowed = interactionFilters
                .stream().
                noneMatch(interactionFilter -> interactionFilter.isAllowedWithin(interactionContext));

        if (isFurtherInteractionNotAllowed) {
            return;
        }

        if (TelegramUtils.isCommand(update)) {
            String command = TelegramUtils.extractCommandString(update);
            Optional.ofNullable(commandHandlers.get(command))
                    .orElseThrow(() -> new IllegalStateException("Unknown command handler"))
                    .handle(telegramMapper.toCommand(update));
            return;
        }


        if (conversationContextService.hasActiveCommand(TelegramUtils.userId(update))) {
            String commandCallback = conversationContextService.fetchCallbackForActiveCommand(TelegramUtils.userId(update)).orElseThrow();
            Optional.ofNullable(callbackHandlers.get(commandCallback))
                    .orElseThrow(() -> new IllegalStateException("Unknown callback handler"))
                    .handle(telegramMapper.toCallbackContext(update));
        }
    }
}
