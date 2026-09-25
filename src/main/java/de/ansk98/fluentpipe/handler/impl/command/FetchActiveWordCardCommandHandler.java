package de.ansk98.fluentpipe.handler.impl.command;

import de.ansk98.fluentpipe.handler.api.command.Command;
import de.ansk98.fluentpipe.handler.api.command.CommandHandler;
import de.ansk98.fluentpipe.service.api.TelegramClient;
import de.ansk98.fluentpipe.service.api.WordCardService;
import de.ansk98.fluentpipe.service.api.commands.FetchActiveWordCardCommand;
import de.ansk98.fluentpipe.service.api.commands.SendMessageCommand;
import de.ansk98.fluentpipe.service.api.dto.WordCardDto;
import org.springframework.stereotype.Component;

/**
 * Command to fetch a currently active card for a user.
 *
 * @author ansk98
 */
@Component
public class FetchActiveWordCardCommandHandler implements CommandHandler {

    private final WordCardService wordCardService;
    private final TelegramClient telegramClient;

    /**
     * Constructor.
     *
     * @param wordCardService See {@link WordCardService}
     * @param telegramClient  See {@link TelegramClient}
     */
    public FetchActiveWordCardCommandHandler(WordCardService wordCardService, TelegramClient telegramClient) {
        this.wordCardService = wordCardService;
        this.telegramClient = telegramClient;
    }

    @Override
    public String supportedCommand() {
        return "/active_word_card";
    }

    @Override
    public void handle(Command command) {
        WordCardDto card = wordCardService.fetchActiveWordCard(new FetchActiveWordCardCommand(command.ownerId()));

        String prettyPrintedNotPublishedActiveCard = card.prettyPrint();
        if (prettyPrintedNotPublishedActiveCard.isBlank()) {
            telegramClient.sendMessage(new SendMessageCommand(command.channelId(), "Noch keine Wörter in der aktiven Wortschatzkarte."));
        } else {
            telegramClient.sendMessage(new SendMessageCommand(command.channelId(), prettyPrintedNotPublishedActiveCard));
        }
    }
}