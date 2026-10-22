package de.ansk98.fluentpipe;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.metadata.MetadataStore;
import org.springframework.integration.metadata.SimpleMetadataStore;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Importable test configuration.
 *
 * @author ansk98
 */
@TestConfiguration
public class FluentPipeTestConfiguration {

    @Bean
    public TelegramBotsLongPollingApplication telegramBotsApplication() {
        // Safe mock instead of trying to connect to Telegram's servers
        return Mockito.mock(TelegramBotsLongPollingApplication.class);
    }

    @Bean
    public TelegramClient telegramClient() {
        // Safe mock client for tests
        return Mockito.mock(TelegramClient.class);
    }

    @Bean
    public MetadataStore metadataStore() {
        return new SimpleMetadataStore();
    }
}
