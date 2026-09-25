package de.ansk98.fluentpipe.handler.impl.command;

import de.ansk98.fluentpipe.handler.api.command.Command;
import de.ansk98.fluentpipe.handler.api.command.StatefulCommandHandler;
import de.ansk98.fluentpipe.service.api.ConversationContextService;
import de.ansk98.fluentpipe.service.api.TelegramClient;
import de.ansk98.fluentpipe.service.api.commands.SendMessageCommand;
import org.springframework.stereotype.Component;

/**
 * Command to add a new word into a word card.
 *
 * @author ansk98
 */
@Component
public class PromptNewWordCommandHandler extends StatefulCommandHandler {

    private final TelegramClient telegramClient;


    /**
     * Constructor.
     *
     * @param conversationContextService See {@link ConversationContextService}
     * @param telegramClient             See {@link TelegramClient}
     */
    protected PromptNewWordCommandHandler(ConversationContextService conversationContextService,
                                          TelegramClient telegramClient) {
        super(conversationContextService);
        this.telegramClient = telegramClient;
    }

    @Override
    public void handleStateful(Command command) {
        telegramClient.sendMessage(new SendMessageCommand(command.channelId(), "Welches Wort? 🤔"));
    }

    @Override
    public String supportedCommand() {
        return "/add_term";
    }
}
