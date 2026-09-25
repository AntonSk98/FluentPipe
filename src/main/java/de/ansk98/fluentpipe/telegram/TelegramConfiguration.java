package de.ansk98.fluentpipe.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Instantiates Telegram Bots API and Telegram Client.
 *
 * @author ansk98
 */
@Configuration
class TelegramConfiguration {

    @Value("${telegram.bot-token}")
    private String botToken;

    /**
     * Creates and registers the Telegram long polling application as a Spring Bean.
     *
     * @param fluentPipeBot {@link FluentPipeBot}
     */
    @Bean
    TelegramBotsLongPollingApplication telegramBotsApplication(FluentPipeBot fluentPipeBot) throws TelegramApiException {
        TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
        botsApplication.registerBot(botToken, fluentPipeBot);
        return botsApplication;
    }

    /**
     * Registers a Telegram Client.
     *
     * @return telegram client
     */
    @Bean
    TelegramClient telegramClient() {
        return new OkHttpTelegramClient(botToken);
    }
}
