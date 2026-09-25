package de.ansk98.fluentpipe.service.impl.pipe;

import de.ansk98.fluentpipe.service.api.IDocumentPipe;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Pipe that splits a PDF document card into images.
 *
 * @author ansk98
 */
@Component
public class PdfToImagesWordCardPipe implements IDocumentPipe<WordCardPdf, WordCardImages> {

    private static final float DPI = 150f;
    private static final String IMAGE_FORMAT = "png";

    @Override

    public WordCardImages pipe(WordCardPdf wordCardPdf) {
        try (PDDocument document = Loader.loadPDF(wordCardPdf.document())) {
            PDFRenderer renderer = new PDFRenderer(document);
            List<byte[]> images = new ArrayList<>();
            for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
                images.add(renderPageAsPng(renderer, pageIndex));
            }
            return new WordCardImages(images);
        } catch (IOException e) {
            throw new RuntimeException("Could not convert PDF to images", e);
        }
    }

    private byte[] renderPageAsPng(PDFRenderer renderer, int pageIndex) throws IOException {
        BufferedImage image = renderer.renderImageWithDPI(pageIndex, DPI);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, IMAGE_FORMAT, outputStream);
        return outputStream.toByteArray();
    }
}