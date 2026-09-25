package de.ansk98.fluentpipe.handler.impl.callback;

import de.ansk98.fluentpipe.config.AiPromptProperties;
import de.ansk98.fluentpipe.handler.api.callback.CallbackContext;
import de.ansk98.fluentpipe.handler.api.callback.StatefulCallbackHandler;
import de.ansk98.fluentpipe.service.api.AiClient;
import de.ansk98.fluentpipe.service.api.ConversationContextService;
import de.ansk98.fluentpipe.service.api.TelegramClient;
import de.ansk98.fluentpipe.service.api.WordCardService;
import de.ansk98.fluentpipe.service.api.commands.AddWordCommand;
import de.ansk98.fluentpipe.service.api.commands.SendMessageCommand;
import de.ansk98.fluentpipe.service.api.dto.AnalyzedWordDto;
import de.ansk98.fluentpipe.service.api.dto.WordDto;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Callback when a new word is provided to be added to a word card.
 *
 * @author ansk98
 */
@Component
public class OnWordProvidedCallbackHandler extends StatefulCallbackHandler {

    private final WordCardService wordCardService;
    private final AiClient aiClient;
    private final AiPromptProperties aiPromptProperties;
    private final TelegramClient telegramClient;

    /**
     * Constructor.
     *
     * @param conversationContextService See {@link ConversationContextService}
     * @param wordCardService            See {@link WordCardService}
     * @param aiClient                   See {@link AiClient}
     * @param aiPromptProperties         See {@link AiPromptProperties}
     * @param telegramClient             See {@link TelegramClient}
     */
    protected OnWordProvidedCallbackHandler(ConversationContextService conversationContextService,
                                            WordCardService wordCardService,
                                            AiClient aiClient,
                                            AiPromptProperties aiPromptProperties,
                                            TelegramClient telegramClient) {
        super(conversationContextService);
        this.wordCardService = wordCardService;
        this.aiClient = aiClient;
        this.aiPromptProperties = aiPromptProperties;
        this.telegramClient = telegramClient;
    }

    @Override
    public void handleStateful(CallbackContext context) {
        AnalyzedWordDto analyzedWord = aiClient.execute(
                aiPromptProperties.prompts().analyzeWord(),
                Map.of("INPUT_WORD", context.input()),
                AnalyzedWordDto.class);

        AddWordCommand addWordCommand = new AddWordCommand(
                context.ownerId(),
                analyzedWord.word(),
                analyzedWord.translation(),
                analyzedWord.meaning(),
                analyzedWord.frequency(),
                analyzedWord.example(),
                analyzedWord.exampleTranslation());

        WordDto addedWord = wordCardService.addWordToCard(addWordCommand);

        telegramClient.sendMessageWithJsonPayload(new SendMessageCommand(context.channelId(), "Zack, im Wortschatz gelandet! ⚡", addedWord));
    }

    @Override
    public String supportedCallbackKey() {
        return "/on_word_provided";
    }
}
