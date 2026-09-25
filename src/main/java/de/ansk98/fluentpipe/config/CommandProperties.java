package de.ansk98.fluentpipe.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Objects;

/**
 * Defines commands and their callbacks.
 *
 * @author ansk98
 */
@ConfigurationProperties
public record CommandProperties(List<CommandMapping> commands) {

    /**
     * Finds a callback key that is held by the passed command key.
     *
     * @param activeCommand active command key
     * @return callback key
     */
    public String requireCallbackFor(String activeCommand) {
        Objects.requireNonNull(activeCommand);


        return commands.stream()
                .filter(command -> activeCommand.equals(command.command()))
                .map(CommandMapping::callback)
                .findFirst()
                .orElseThrow();
    }

    /**
     * Represents a single command-to-callback mapping configuration.
     */
    public record CommandMapping(String command, String callback) {
    }
}