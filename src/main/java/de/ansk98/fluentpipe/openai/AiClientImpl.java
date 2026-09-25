package de.ansk98.fluentpipe.openai;

import de.ansk98.fluentpipe.config.AiPromptProperties;
import de.ansk98.fluentpipe.service.api.AiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.stereotype.Service;

import javax.sound.sampled.AudioFormat;
import java.util.Collection;
import java.util.Map;

/**
 * Implementation of {@link AiClient}.
 *
 * @author ansk98
 */
@Service
public class AiClientImpl implements AiClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AiClientImpl.class);

    /**
     * Pause between two spoken words in seconds.
     */
    private static final double WORD_PAUSE_SECONDS = 1.0;

    /**
     * Target PCM format used to combine the synthesized words into a single audio.
     */
    private static final AudioFormat TARGET_FORMAT = new AudioFormat(44_100f, 16, 1, true, false);

    private final ChatClient chatClient;
    private final OpenAiAudioSpeechModel audioSpeechModel;

    /**
     * Constructor.
     *
     * @param chatClientBuilder the chat client builder
     * @param audioSpeechModel  the model used to synthesize the German words
     */
    public AiClientImpl(ChatClient.Builder chatClientBuilder, OpenAiAudioSpeechModel audioSpeechModel) {
        this.chatClient = chatClientBuilder.build();
        this.audioSpeechModel = audioSpeechModel;
    }

    @Override
    public <T> T execute(String template, Map<String, String> variables, Class<T> responseType) {
        try {
            String prompt = AiPromptProperties.resolve(template, variables);
            return chatClient.prompt().user(prompt).call().entity(responseType);
        } catch (Exception e) {
            LOGGER.error("Failed to execute AI prompt", e);
            throw e;
        }
    }

    @Override
    public byte[] synthesizeWordCardAudio(Collection<String> words) {
        // Ellipses create natural pauses between German words in gpt-4o-mini-tts
        String promptText = String.join("...\n—\n\n", words);

        // Spring AI automatically injects options defined in application.properties
        return audioSpeechModel.call(new TextToSpeechPrompt(promptText))
                .getResult()
                .getOutput();
    }
}
