package de.ansk98.fluentpipe.telegram;

import de.ansk98.fluentpipe.service.api.TelegramClient;
import de.ansk98.fluentpipe.service.api.commands.PublishWordCardWithAudioCommand;
import de.ansk98.fluentpipe.service.api.commands.SendImagesWithCaptionCommand;
import de.ansk98.fluentpipe.service.api.commands.SendMessageCommand;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.ReplyParameters;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tools.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of {@link TelegramClient} delegating to the Telegram Bot API.
 *
 * @author ansk98
 */
@Component
public class TelegramClientImpl implements TelegramClient {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final org.telegram.telegrambots.meta.generics.TelegramClient client;

    /**
     * Constructor.
     *
     * @param client the underlying Telegram Bot API client
     */
    public TelegramClientImpl(org.telegram.telegrambots.meta.generics.TelegramClient client) {
        this.client = client;
    }

    @Override
    public void sendMessage(SendMessageCommand sendMessageCommand) {
        SendMessage sendMessage = new SendMessage(sendMessageCommand.channelId(), sendMessageCommand.message());
        execute(sendMessage);
    }

    @Override
    public void sendMessageWithJsonPayload(SendMessageCommand sendMessageCommand) {
        String payload = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(sendMessageCommand.payload());
        String htmlPayload = "<pre><code class=\"language-json\">" + escapeHtml(payload) + "</code></pre>";
        SendMessage sendMessage = new SendMessage(sendMessageCommand.channelId(), sendMessageCommand.message() + "\n" + htmlPayload);
        sendMessage.setParseMode("HTML");
        execute(sendMessage);
    }

    @Override
    public void sendImagesWithCaption(SendImagesWithCaptionCommand command) {
        publishCardImages(command.channelId(), command.caption(), command.imageBinaries());
    }

    @Override
    public void publishWordCardWithAudio(PublishWordCardWithAudioCommand command) {
        Message parentMessage = publishCardImages(command.channelId(), command.caption(), command.imageBinaries());

        if (parentMessage != null && command.audio() != null) {
            publishAudioReply(command.channelId(), parentMessage.getMessageId(), command.audio());
        }
    }

    private Message publishCardImages(String channelId, String caption, List<byte[]> images) {
        if (images.isEmpty()) {
            return null;
        }
        return images.size() == 1
                ? sendSinglePhoto(channelId, caption, images.getFirst())
                : sendMediaGroups(channelId, caption, images);
    }

    private Message sendSinglePhoto(String channelId, String caption, byte[] imageBytes) {
        InputFile photoFile = new InputFile(new ByteArrayInputStream(imageBytes), "card.jpg");
        SendPhoto sendPhoto = SendPhoto.builder()
                .chatId(channelId)
                .photo(photoFile)
                .caption(caption)
                .build();

        try {
            return client.execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new IllegalStateException("Failed to send photo card", e);
        }
    }

    private Message sendMediaGroups(String channelId, String caption, List<byte[]> images) {
        int groupSize = 10;
        Message firstMessage = null;

        for (int offset = 0; offset < images.size(); offset += groupSize) {
            List<byte[]> group = images.subList(offset, Math.min(offset + groupSize, images.size()));
            boolean isFirstGroup = (offset == 0);

            List<Message> sentGroup = sendSingleMediaGroup(channelId, isFirstGroup ? caption : null, group);

            if (isFirstGroup && !sentGroup.isEmpty()) {
                firstMessage = sentGroup.getFirst();
            }
        }

        return firstMessage;
    }

    private List<Message> sendSingleMediaGroup(String channelId, String caption, List<byte[]> imageGroup) {
        List<InputMedia> mediaList = new ArrayList<>();

        for (int i = 0; i < imageGroup.size(); i++) {
            InputMediaPhoto photo = new InputMediaPhoto(new ByteArrayInputStream(imageGroup.get(i)), "image_" + i + ".jpg");
            if (i == 0 && caption != null) {
                photo.setCaption(caption);
            }
            mediaList.add(photo);
        }

        SendMediaGroup sendMediaGroup = SendMediaGroup.builder()
                .chatId(channelId)
                .medias(mediaList)
                .build();

        try {
            return client.execute(sendMediaGroup);
        } catch (TelegramApiException e) {
            throw new IllegalStateException("Failed to send media group", e);
        }
    }

    private void publishAudioReply(String channelId, Integer replyToMessageId, byte[] audio) {
        InputFile audioFile = new InputFile(
                new ByteArrayInputStream(audio),
                "daily_deutsch_aussprache.wav"
        );

        SendAudio sendAudio = SendAudio.builder()
                .chatId(channelId)
                .audio(audioFile)
                .performer("@daily_deutsch_group")
                .replyParameters(ReplyParameters.builder()
                        .messageId(replyToMessageId)
                        .build())
                .disableNotification(true)
                .build();

        try {
            client.execute(sendAudio);
        } catch (TelegramApiException e) {
            throw new IllegalStateException("Failed to send audio reply", e);
        }
    }

    /**
     * Escapes HTML special characters for safe rendering in messages.
     *
     * @param value the raw text
     * @return the escaped text
     */
    private static String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    /**
     * Executes a send-message request, wrapping any API exception.
     *
     * @param sendMessage the send message request
     */
    private void execute(SendMessage sendMessage) {
        try {
            client.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new IllegalStateException(e);
        }
    }
}
