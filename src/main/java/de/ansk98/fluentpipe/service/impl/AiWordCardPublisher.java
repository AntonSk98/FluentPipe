package de.ansk98.fluentpipe.service.impl;

import de.ansk98.fluentpipe.config.AiPromptProperties;
import de.ansk98.fluentpipe.service.api.AiClient;
import de.ansk98.fluentpipe.service.api.TelegramClient;
import de.ansk98.fluentpipe.service.api.WordCardService;
import de.ansk98.fluentpipe.service.api.commands.AddWordCommand;
import de.ansk98.fluentpipe.service.api.commands.FetchActiveWordCardCommand;
import de.ansk98.fluentpipe.service.api.commands.PublishWordCardWithAudioCommand;
import de.ansk98.fluentpipe.service.api.dto.AnalyzedWordDto;
import de.ansk98.fluentpipe.service.api.dto.WordDto;
import de.ansk98.fluentpipe.service.impl.pipe.WordCardImages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Publishes a word card that is created by AI with a regular time intervals.
 *
 * @author ansk98
 */
@Service
public class AiWordCardPublisher {

    @Value("${telegram.channel-id}")
    private String groupChannelId;

    private static final String AI_OWNER_ID = "ai";

    private final AiPromptProperties aiPromptProperties;
    private final AiClient aiClient;
    private final TelegramClient telegramClient;
    private final WordCardService wordCardService;
    private final WordCardRenderer wordCardRenderer;


    /**
     * Constructor.
     *
     * @param aiPromptProperties See {@link AiPromptProperties}
     * @param aiClient           See {@link AiClient}
     * @param telegramClient     See {@link TelegramClient}
     * @param wordCardService    See {@link WordCardService}
     * @param wordCardRenderer   See {@link WordCardRenderer}
     */
    public AiWordCardPublisher(AiPromptProperties aiPromptProperties,
                               AiClient aiClient,
                               TelegramClient telegramClient,
                               WordCardService wordCardService,
                               WordCardRenderer wordCardRenderer) {
        this.aiPromptProperties = aiPromptProperties;
        this.aiClient = aiClient;
        this.telegramClient = telegramClient;
        this.wordCardService = wordCardService;
        this.wordCardRenderer = wordCardRenderer;
    }

    @Scheduled(cron = "0 0 8,17 * * *")
    public void publishAiDrivenWordCard() {
        wordCardService.clearNotPublishedWordCard(AI_OWNER_ID);

        List<String> toBeAddedWords = aiClient.execute(
                aiPromptProperties.prompts().aiWordCard(),
                Map.of(
                        "DAY_OF_WEEK", LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMANY),
                        "TODAY_DATE", LocalDate.now().toString(),
                        "TIME_SLOT", LocalTime.now().getHour() < 12 ? "MORNING_LESSON" : "EVENING_LESSON",
                        "RANDOM_SEED", UUID.randomUUID().toString()
                ),
                List.class);

        for (String toBeAddedWord : toBeAddedWords) {
            AnalyzedWordDto analyzedWord = aiClient.execute(
                    aiPromptProperties.prompts().analyzeWord(),
                    Map.of("INPUT_WORD", toBeAddedWord),
                    AnalyzedWordDto.class);

            wordCardService.addWordToCard(
                    new AddWordCommand(
                            AI_OWNER_ID,
                            analyzedWord.word(),
                            analyzedWord.translation(),
                            analyzedWord.meaning(),
                            analyzedWord.frequency(),
                            analyzedWord.example(),
                            analyzedWord.exampleTranslation())
            );
        }


        var aiGeneratedWordCard = wordCardService.fetchActiveWordCard(new FetchActiveWordCardCommand(AI_OWNER_ID));

        WordCardImages wordCardImages = wordCardRenderer.renderWordCardAsImages(aiGeneratedWordCard);
        byte[] wordCardAudio = aiClient.synthesizeWordCardAudio(aiGeneratedWordCard.words().stream().map(WordDto::word).collect(Collectors.toList()));

        telegramClient.publishWordCardWithAudio(new PublishWordCardWithAudioCommand(groupChannelId, "#WortschatzDesTages#KI-Edition", wordCardAudio, wordCardImages.images()));
        wordCardService.publishWordCard(AI_OWNER_ID);
    }
}
