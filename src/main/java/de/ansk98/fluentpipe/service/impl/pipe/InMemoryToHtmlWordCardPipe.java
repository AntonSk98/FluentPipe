package de.ansk98.fluentpipe.service.impl.pipe;

import de.ansk98.fluentpipe.domain.WordCard;
import de.ansk98.fluentpipe.service.api.IDocumentPipe;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

/**
 * Pipe that renders a {@link WordCard} into an HTML document.
 *
 * @author ansk98
 */
@Component
public class InMemoryToHtmlWordCardPipe implements IDocumentPipe<InMemoryWordCard, WordCardHtml> {

    private final SpringTemplateEngine springTemplateEngine;

    /**
     * Constructor.
     *
     * @param springTemplateEngine See {@link SpringTemplateEngine}
     */
    public InMemoryToHtmlWordCardPipe(SpringTemplateEngine springTemplateEngine) {
        this.springTemplateEngine = springTemplateEngine;
    }

    /**
     * Renders the word card template with the given card as context.
     *
     * @param wordCard the word card to render
     * @return the generated HTML
     */
    public WordCardHtml pipe(InMemoryWordCard wordCard) {
        Context context = new Context();
        context.setVariable("wordCard", wordCard);
        var html = springTemplateEngine.process("word_card_template", context);
        return new WordCardHtml(html);
    }
}