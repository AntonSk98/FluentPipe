package de.ansk98.fluentpipe.handler.impl.command;

import de.ansk98.fluentpipe.handler.api.command.Command;
import de.ansk98.fluentpipe.handler.api.command.CommandHandler;
import de.ansk98.fluentpipe.service.api.TelegramClient;
import de.ansk98.fluentpipe.service.api.commands.SendImagesWithCaptionCommand;
import de.ansk98.fluentpipe.service.api.commands.SendMessageCommand;
import de.ansk98.fluentpipe.service.api.dto.WordCardDto;
import de.ansk98.fluentpipe.service.impl.WordCardRenderer;
import de.ansk98.fluentpipe.service.impl.pipe.WordCardImages;
import org.springframework.stereotype.Component;

/**
 * Command that renders the active word card as an image preview.
 *
 * @author ansk98
 */
@Component
public class PreviewWordCardCommandHandler implements CommandHandler {

    private final WordCardRenderer wordCardRenderer;
    private final TelegramClient telegramClient;

    /**
     * Constructor.
     *
     * @param wordCardRenderer See {@link WordCardRenderer}
     * @param telegramClient   See {@link TelegramClient}
     */
    public PreviewWordCardCommandHandler(WordCardRenderer wordCardRenderer, TelegramClient telegramClient) {
        this.wordCardRenderer = wordCardRenderer;
        this.telegramClient = telegramClient;
    }

    @Override
    public void handle(Command command) {
        WordCardDto card = wordCardRenderer.fetchActiveWordCard(command.ownerId());

        if (card.prettyPrint().isBlank()) {
            telegramClient.sendMessage(new SendMessageCommand(command.channelId(),
                    "Deine Wortschatzkarte ist noch leer – füge zuerst ein paar Wörter hinzu! ✍️"));
            return;
        }

        WordCardImages images = wordCardRenderer.renderWordCardAsImages(card);
        telegramClient.sendImagesWithCaption(new SendImagesWithCaptionCommand(command.channelId(), "Deine aktuelle Wortschatzkarte 📚", images.images()));
    }

    @Override
    public String supportedCommand() {
        return "/preview_word_card";
    }
}