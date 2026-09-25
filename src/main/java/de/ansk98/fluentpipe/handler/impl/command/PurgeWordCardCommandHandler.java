package de.ansk98.fluentpipe.handler.impl.command;

import de.ansk98.fluentpipe.handler.api.command.Command;
import de.ansk98.fluentpipe.handler.api.command.CommandHandler;
import de.ansk98.fluentpipe.service.api.TelegramClient;
import de.ansk98.fluentpipe.service.api.WordCardService;
import de.ansk98.fluentpipe.service.api.commands.SendMessageCommand;
import org.springframework.stereotype.Component;

/**
 * Command to clear all words from the currently active word card.
 *
 * @author ansk98
 */
@Component
public class PurgeWordCardCommandHandler implements CommandHandler {

    private final WordCardService wordCardService;
    private final TelegramClient telegramClient;

    /**
     * Constructor.
     *
     * @param wordCardService See {@link WordCardService}
     * @param telegramClient  See {@link TelegramClient}
     */
    public PurgeWordCardCommandHandler(WordCardService wordCardService, TelegramClient telegramClient) {
        this.wordCardService = wordCardService;
        this.telegramClient = telegramClient;
    }

    @Override
    public String supportedCommand() {
        return "/purge_word_card";
    }

    @Override
    public void handle(Command command) {
        wordCardService.clearNotPublishedWordCard(command.ownerId());
        telegramClient.sendMessage(new SendMessageCommand(command.channelId(),
                "Deine Wortschatzkarte wurde geleert. 🧹"));
    }
}