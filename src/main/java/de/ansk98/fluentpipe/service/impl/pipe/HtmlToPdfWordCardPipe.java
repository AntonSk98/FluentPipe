package de.ansk98.fluentpipe.service.impl.pipe;

import com.openhtmltopdf.extend.FSSupplier;
import com.openhtmltopdf.outputdevice.helper.BaseRendererBuilder.FontStyle;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import de.ansk98.fluentpipe.service.api.IDocumentPipe;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Pipe that converts an HTML document into a PDF.
 *
 * @author ansk98
 */
@Component
public class HtmlToPdfWordCardPipe implements IDocumentPipe<WordCardHtml, WordCardPdf> {

    private static final String FONT_FAMILY = "DejaVu Sans";

    /**
     * Converts the given HTML into PDF bytes.
     *
     * @param html the HTML document to convert
     * @return the generated PDF bytes
     */
    public WordCardPdf pipe(WordCardHtml html) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(html.wordCardHtml(), null);
        registerFonts(builder);
        builder.toStream(outputStream);
        try {
            builder.run();
        } catch (IOException e) {
            throw new RuntimeException("Could not convert HTML to PDF", e);
        }
        return new WordCardPdf(outputStream.toByteArray());
    }

    private void registerFonts(PdfRendererBuilder builder) {
        builder.useFont(font("/fonts/DejaVuSans.ttf"), FONT_FAMILY, 400, FontStyle.NORMAL, true);
        builder.useFont(font("/fonts/DejaVuSans-Bold.ttf"), FONT_FAMILY, 700, FontStyle.NORMAL, true);
        builder.useFont(font("/fonts/DejaVuSans-Oblique.ttf"), FONT_FAMILY, 400, FontStyle.ITALIC, true);
        builder.useFont(font("/fonts/DejaVuSans-BoldOblique.ttf"), FONT_FAMILY, 700, FontStyle.ITALIC, true);
    }

    private FSSupplier<InputStream> font(String path) {
        return () -> getClass().getResourceAsStream(path);
    }
}