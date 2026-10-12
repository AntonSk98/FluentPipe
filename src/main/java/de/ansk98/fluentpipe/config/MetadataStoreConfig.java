package de.ansk98.fluentpipe.config;

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
public class MetadataStoreConfig {

    /**
     * Creates a file-persisting {@link MetadataStore} for feed history.
     *
     * @return a properties-backed metadata store saving to {@code build/metadata-store/feed-history.properties}
     */
    @Bean
    public MetadataStore metadataStore() {
        PropertiesPersistingMetadataStore store = new PropertiesPersistingMetadataStore();

        store.setBaseDirectory("build/metadata-store");
        store.setFileName("feed-history.properties");

        return store;
    }
}