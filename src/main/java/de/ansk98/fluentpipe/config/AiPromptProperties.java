package de.ansk98.fluentpipe.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * Configuration properties for the AI prompt templates.
 *
 * @author ansk98
 */
@ConfigurationProperties(prefix = "ai")
public record AiPromptProperties(PromptTemplates prompts) {

    /**
     * Prompt templates used to drive the AI client.
     *
     * @param analyzeWord the prompt template used to analyze a word and derive its
     *                    translation, definition, usage frequency and an example sentence
     */
    public record PromptTemplates(String analyzeWord) {
    }

    /**
     * Resolves all template variables of the form {@code ${VARIABLE_NAME}} within the given
     * template, e.g. {@code "${INPUT_WORD}"}.
     *
     * @param template  the raw prompt template containing variables to resolve
     * @param variables map between variable names (without the {@code ${...}} wrapper)
     *                  and their replacement values
     * @return the template with every variable replaced by its value
     */
    public static String resolve(String template, Map<String, String> variables) {
        String resolved = template;
        for (Map.Entry<String, String> variable : variables.entrySet()) {
            resolved = resolved.replace("${" + variable.getKey() + "}", variable.getValue());
        }
        return resolved;
    }
}