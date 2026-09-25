package de.ansk98.fluentpipe.service.api;

import java.util.Collection;
import java.util.Map;

/**
 * High-level generic client for executing AI prompts and parsing structured responses. *
 *
 * @author ansk98
 */
public interface AiClient {

    /**
     * Executes a configured prompt template against the AI provider and parses the
     * structured response into the specified type.
     *
     * @param template     the configured prompt template, possibly containing {@code ${VARIABLE}} placeholders
     * @param variables    the values used to resolve the template placeholders
     * @param responseType the class type of the expected record or object response
     * @param <T>          the target object type
     * @return the parsed response object mapped to type {@code T}
     */
    <T> T execute(String template, Map<String, String> variables, Class<T> responseType);

    /**
     * Synthesizes the given words as a single audio, separated by natural pauses.
     *
     * @param words the words to speak
     * @return the audio binary
     */
    byte[] synthesizeWordCardAudio(Collection<String> words);
}
