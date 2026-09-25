package de.ansk98.fluentpipe.handler.impl.command;

import de.ansk98.fluentpipe.handler.api.command.Command;
import de.ansk98.fluentpipe.handler.api.command.StatefulCommandHandler;
import de.ansk98.fluentpipe.service.api.ConversationContextService;
import de.ansk98.fluentpipe.service.api.TelegramClient;
import de.ansk98.fluentpipe.service.api.WordCardService;
import de.ansk98.fluentpipe.service.api.commands.FetchActiveWordCardCommand;
import de.ansk98.fluentpipe.service.api.commands.SendMessageCommand;
import de.ansk98.fluentpipe.service.api.dto.WordCardDto;
import org.springframework.stereotype.Component;

/**
 * Command that shows the active word card and prompts which word to remove.
 *
 * @author ansk98
 */
@Component
public class RemoveTermCommandHandler extends StatefulCommandHandler {

    private final WordCardService wordCardService;
    private final TelegramClient telegramClient;

    /**
     * Constructor.
     *
     * @param conversationContextService See {@link ConversationContextService}
     * @param wordCardService            See {@link WordCardService}
     * @param telegramClient             See {@link TelegramClient}
     */
    protected RemoveTermCommandHandler(ConversationContextService conversationContextService,
                                       WordCardService wordCardService,
                                       TelegramClient telegramClient) {
        super(conversationContextService);
        this.wordCardService = wordCardService;
        this.telegramClient = telegramClient;
    }

    @Override
    public void handleStateful(Command command) {
        WordCardDto card = wordCardService.fetchActiveWordCard(new FetchActiveWordCardCommand(command.ownerId()));

        String content = card.prettyPrint();
        String message = content.isBlank()
                ? "Deine Wortschatzkarte ist leer – füge zuerst ein paar Wörter hinzu."
                : "Hier ist deine aktuelle Wortschatzkarte:\n\n"
                + content
                + "\n\n✂️ Welches Wort soll gelöscht werden?";
        telegramClient.sendMessage(new SendMessageCommand(command.channelId(), message));
    }

    @Override
    public String supportedCommand() {
        return "/remove_term";
    }
}