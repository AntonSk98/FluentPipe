package de.ansk98.fluentpipe.handler.impl.command;

import de.ansk98.fluentpipe.handler.api.command.Command;
import de.ansk98.fluentpipe.handler.api.command.StatefulCommandHandler;
import de.ansk98.fluentpipe.handler.impl.callback.OnWordCardPublishDecidedCallbackHandler;
import de.ansk98.fluentpipe.service.api.ConversationContextService;
import de.ansk98.fluentpipe.service.api.TelegramClient;
import de.ansk98.fluentpipe.service.api.commands.SendMessageCommand;
import de.ansk98.fluentpipe.service.api.dto.WordCardDto;
import de.ansk98.fluentpipe.service.impl.WordCardRenderer;
import org.springframework.stereotype.Component;

/**
 * Command that asks the user to confirm publishing the active word card.
 *
 * @author ansk98
 */
@Component
public class PublishWordCardCommandHandler extends StatefulCommandHandler {

    private final WordCardRenderer wordCardRenderer;
    private final TelegramClient telegramClient;

    /**
     * Constructor.
     *
     * @param conversationContextService See {@link ConversationContextService}
     * @param wordCardRenderer           See {@link WordCardRenderer}
     * @param telegramClient             See {@link TelegramClient}
     */
    protected PublishWordCardCommandHandler(ConversationContextService conversationContextService,
                                            WordCardRenderer wordCardRenderer,
                                            TelegramClient telegramClient) {
        super(conversationContextService);
        this.wordCardRenderer = wordCardRenderer;
        this.telegramClient = telegramClient;
    }

    @Override
    public void handleStateful(Command command) {
        WordCardDto card = wordCardRenderer.fetchActiveWordCard(command.ownerId());

        if (card.prettyPrint().isBlank()) {
            telegramClient.sendMessage(new SendMessageCommand(command.channelId(),
                    "Deine Wortschatzkarte ist noch leer – füge zuerst ein paar Wörter hinzu! ✍️"));
            return;
        }

        telegramClient.sendMessage(new SendMessageCommand(command.channelId(),
                "Deine Wortschatzkarte ist bereit zur Veröffentlichung! 🎉\n\nSende einfach '"
                        + OnWordCardPublishDecidedCallbackHandler.PUBLISH_CONFIRMATION
                        + "', um sie zu veröffentlichen."));
    }

    @Override
    public String supportedCommand() {
        return "/publish_word_card";
    }
}