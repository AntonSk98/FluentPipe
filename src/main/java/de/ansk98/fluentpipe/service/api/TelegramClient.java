package de.ansk98.fluentpipe.service.api;

import de.ansk98.fluentpipe.service.api.commands.PublishWordCardWithAudioCommand;
import de.ansk98.fluentpipe.service.api.commands.SendImagesWithCaptionCommand;
import de.ansk98.fluentpipe.service.api.commands.SendMessageCommand;

/**
 * Defines a technology-agnostic client abstraction for sending messages,
 * handling updates, and interacting with the Telegram Bot API.
 *
 * @author ansk98
 */
public interface TelegramClient {

    /**
     * Sends a plain message to a user.
     *
     * @param sendMessageCommand command
     */
    void sendMessage(SendMessageCommand sendMessageCommand);

    /**
     * Sends a message to a user with the command's payload serialized as JSON
     * and appended after the message text.
     *
     * @param sendMessageCommand command
     */
    void sendMessageWithJsonPayload(SendMessageCommand sendMessageCommand);

    /**
     * Sends a group of images with a caption (title) to a user.
     *
     * @param sendImagesWithCaptionCommand command
     */
    void sendImagesWithCaption(SendImagesWithCaptionCommand sendImagesWithCaptionCommand);

    /**
     * Sends a word card to a channel as images with a caption, plus an audio of the spoken words.
     *
     * @param publishWordCardWithAudioCommand command
     */
    void publishWordCardWithAudio(PublishWordCardWithAudioCommand publishWordCardWithAudioCommand);
}