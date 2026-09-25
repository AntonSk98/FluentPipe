package de.ansk98.fluentpipe.handler.impl.callback;

import de.ansk98.fluentpipe.handler.api.callback.CallbackContext;
import de.ansk98.fluentpipe.handler.api.callback.StatefulCallbackHandler;
import de.ansk98.fluentpipe.service.api.AiClient;
import de.ansk98.fluentpipe.service.api.ConversationContextService;
import de.ansk98.fluentpipe.service.api.TelegramClient;
import de.ansk98.fluentpipe.service.api.WordCardService;
import de.ansk98.fluentpipe.service.api.commands.PublishWordCardWithAudioCommand;
import de.ansk98.fluentpipe.service.api.commands.SendMessageCommand;
import de.ansk98.fluentpipe.service.api.dto.WordCardDto;
import de.ansk98.fluentpipe.service.api.dto.WordDto;
import de.ansk98.fluentpipe.service.impl.WordCardRenderer;
import de.ansk98.fluentpipe.service.impl.pipe.WordCardImages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Callback when the user decided whether to publish the active word card.
 *
 * @author ansk98
 */
@Component
public class OnWordCardPublishDecidedCallbackHandler extends StatefulCallbackHandler {

    public static final String PUBLISH_CONFIRMATION = "+";

    private final WordCardRenderer wordCardRenderer;
    private final WordCardService wordCardService;
    private final TelegramClient telegramClient;
    private final AiClient aiClient;

    @Value("${telegram.channel-id}")
    private String groupChannelId;

    /**
     * Constructor.
     *
     * @param conversationContextService See {@link ConversationContextService}
     * @param wordCardRenderer           See {@link WordCardRenderer}
     * @param wordCardService            See {@link WordCardService}
     * @param telegramClient             See {@link TelegramClient}
     * @param aiClient                   See {@link AiClient}
     */
    protected OnWordCardPublishDecidedCallbackHandler(ConversationContextService conversationContextService,
                                                      WordCardRenderer wordCardRenderer,
                                                      WordCardService wordCardService,
                                                      TelegramClient telegramClient,
                                                      AiClient aiClient) {
        super(conversationContextService);
        this.wordCardRenderer = wordCardRenderer;
        this.wordCardService = wordCardService;
        this.telegramClient = telegramClient;
        this.aiClient = aiClient;
    }

    @Override
    public void handleStateful(CallbackContext context) {
        WordCardDto card = wordCardRenderer.fetchActiveWordCard(context.ownerId());

        if (card.prettyPrint().isBlank()) {
            telegramClient.sendMessage(new SendMessageCommand(context.channelId(), "Keine Wortschatzkarte zum Veröffentlichen vorhanden."));
            return;
        }

        if (!PUBLISH_CONFIRMATION.equals(context.input().trim())) {
            telegramClient.sendMessage(new SendMessageCommand(context.channelId(), "Veröffentlichung abgebrochen..."));
            return;
        }

        WordCardImages wordCardImages = wordCardRenderer.renderWordCardAsImages(card);
        byte[] wordCardAudio = aiClient.synthesizeWordCardAudio(card.words().stream().map(WordDto::word).collect(Collectors.toList()));

        telegramClient.publishWordCardWithAudio(new PublishWordCardWithAudioCommand(groupChannelId, "#WortschatzDesTages", wordCardAudio, wordCardImages.images()));
        wordCardService.publishWordCard(context.ownerId());
        telegramClient.sendMessage(new SendMessageCommand(context.channelId(), "Deine Wortschatzkarte wurde veröffentlicht! 🎉"));
    }

    @Override
    public String supportedCallbackKey() {
        return "/on_word_card_publish_decided";
    }
}