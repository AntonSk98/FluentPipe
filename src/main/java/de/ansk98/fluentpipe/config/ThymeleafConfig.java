package de.ansk98.fluentpipe.config;

import de.ansk98.fluentpipe.service.api.IDocumentPipe;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * Provides a non-web {@link SpringTemplateEngine} used by the rendering pipes.
 * See {@link IDocumentPipe}.
 *
 * @author ansk98
 */
@Configuration
public class ThymeleafConfig {

    /**
     * Creates the {@link ClassLoaderTemplateResolver} resolving templates from the classpath
     * {@code /templates} directory.
     *
     * @return template resolver
     */
    @Bean
    public ClassLoaderTemplateResolver templateResolver() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        return resolver;
    }

    /**
     * Creates the {@link SpringTemplateEngine} bean used to render the HTML templates.
     *
     * @param templateResolver the classpath template resolver
     * @return the template engine
     */
    @Bean
    @ConditionalOnMissingBean(SpringTemplateEngine.class)
    public SpringTemplateEngine springTemplateEngine(ClassLoaderTemplateResolver templateResolver) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver);
        return engine;
    }
}