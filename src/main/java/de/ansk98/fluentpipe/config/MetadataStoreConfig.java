package de.ansk98.fluentpipe.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.metadata.MetadataStore;
import org.springframework.integration.metadata.PropertiesPersistingMetadataStore;

/**
 * Spring configuration for file-based metadata persistence.
 *
 * @author ansk98
 */
@Configuration
@ConditionalOnProperty(prefix = "fluentpipe.metadata-store", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MetadataStoreConfig {

    /**
     * Creates a file-persisting {@link MetadataStore} for feed history.
     *
     * @param baseDirectory directory the store persists to
     * @return a properties-backed metadata store saving to {@code <baseDirectory>/feed-history.properties}
     * @throws IOException if the base directory cannot be created
     */
    @Bean
    public MetadataStore metadataStore(
            @Value("${fluentpipe.metadata-store.base-directory}") String baseDirectory)
            throws IOException {

        Files.createDirectories(Path.of(baseDirectory));

        PropertiesPersistingMetadataStore store = new PropertiesPersistingMetadataStore();

        store.setBaseDirectory(baseDirectory);
        store.setFileName("feed-history.properties");

        return store;
    }
}
