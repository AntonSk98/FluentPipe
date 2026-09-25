package de.ansk98.fluentpipe.service.impl;

import de.ansk98.fluentpipe.service.api.WordCardService;
import de.ansk98.fluentpipe.service.api.commands.FetchActiveWordCardCommand;
import de.ansk98.fluentpipe.service.api.dto.WordCardDto;
import de.ansk98.fluentpipe.service.api.dto.WordDto;
import de.ansk98.fluentpipe.service.impl.pipe.*;
import org.springframework.stereotype.Component;

/**
 * Renders a {@link WordCardDto} into images and fetches the active word card.
 *
 * @author ansk98
 */
@Component
public class WordCardRenderer {

    private final WordCardService wordCardService;
    private final InMemoryToHtmlWordCardPipe inMemoryToHtmlWordCardPipe;
    private final HtmlToPdfWordCardPipe htmlToPdfWordCardPipe;
    private final PdfToImagesWordCardPipe pdfToImagesWordCardPipe;

    /**
     * Constructor.
     *
     * @param wordCardService            See {@link WordCardService}
     * @param inMemoryToHtmlWordCardPipe See {@link InMemoryToHtmlWordCardPipe}
     * @param htmlToPdfWordCardPipe      See {@link HtmlToPdfWordCardPipe}
     * @param pdfToImagesWordCardPipe    See {@link PdfToImagesWordCardPipe}
     */
    public WordCardRenderer(WordCardService wordCardService,
                            InMemoryToHtmlWordCardPipe inMemoryToHtmlWordCardPipe,
                            HtmlToPdfWordCardPipe htmlToPdfWordCardPipe,
                            PdfToImagesWordCardPipe pdfToImagesWordCardPipe) {
        this.wordCardService = wordCardService;
        this.inMemoryToHtmlWordCardPipe = inMemoryToHtmlWordCardPipe;
        this.htmlToPdfWordCardPipe = htmlToPdfWordCardPipe;
        this.pdfToImagesWordCardPipe = pdfToImagesWordCardPipe;
    }

    /**
     * Fetches the active word card of a user.
     *
     * @param ownerId ownerId
     * @return the active word card
     */
    public WordCardDto fetchActiveWordCard(String ownerId) {
        return wordCardService.fetchActiveWordCard(new FetchActiveWordCardCommand(ownerId));
    }

    /**
     * Renders a word card into images.
     *
     * @param card the word card to render
     * @return the word card as images
     */
    public WordCardImages renderWordCardAsImages(WordCardDto card) {
        InMemoryWordCard inMemoryCard = new InMemoryWordCard(
                card.words().stream()
                        .map(this::toInMemoryWord)
                        .toList());
        WordCardHtml html = inMemoryToHtmlWordCardPipe.pipe(inMemoryCard);
        WordCardPdf pdf = htmlToPdfWordCardPipe.pipe(html);
        return pdfToImagesWordCardPipe.pipe(pdf);
    }

    private InMemoryWordCard.InMemoryWord toInMemoryWord(WordDto word) {
        return new InMemoryWordCard.InMemoryWord(
                word.word(),
                word.translation(),
                word.meaning(),
                word.frequency() != null ? word.frequency().getRank() : 0,
                word.example(),
                word.exampleTranslation());
    }
}