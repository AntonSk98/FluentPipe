package de.ansk98.fluentpipe.handler.impl.callback;

import de.ansk98.fluentpipe.handler.api.callback.CallbackContext;
import de.ansk98.fluentpipe.handler.api.callback.StatefulCallbackHandler;
import de.ansk98.fluentpipe.service.api.ConversationContextService;
import de.ansk98.fluentpipe.service.api.TelegramClient;
import de.ansk98.fluentpipe.service.api.WordCardService;
import de.ansk98.fluentpipe.service.api.commands.DeleteWordCommand;
import de.ansk98.fluentpipe.service.api.commands.SendMessageCommand;
import org.springframework.stereotype.Component;

/**
 * Callback when the word to be removed from a word card is provided.
 *
 * @author ansk98
 */
@Component
public class OnTermSelectedForRemovalCallbackHandler extends StatefulCallbackHandler {

    private final WordCardService wordCardService;
    private final TelegramClient telegramClient;

    /**
     * Constructor.
     *
     * @param conversationContextService See {@link ConversationContextService}
     * @param wordCardService            See {@link WordCardService}
     * @param telegramClient             See {@link TelegramClient}
     */
    protected OnTermSelectedForRemovalCallbackHandler(ConversationContextService conversationContextService,
                                                      WordCardService wordCardService,
                                                      TelegramClient telegramClient) {
        super(conversationContextService);
        this.wordCardService = wordCardService;
        this.telegramClient = telegramClient;
    }

    @Override
    public void handleStateful(CallbackContext context) {
        boolean removed = wordCardService.removeWordFromCard(new DeleteWordCommand(context.ownerId(), context.input()));
        String message = removed
                ? "Wort gelöscht! ✂️"
                : "Das Wort wurde nicht gefunden -> nichts entfernt.";
        telegramClient.sendMessage(new SendMessageCommand(context.ownerId(), message));
    }

    @Override
    public String supportedCallbackKey() {
        return "/on_term_selected_for_removal";
    }
}