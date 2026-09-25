package de.ansk98.fluentpipe.service.api;

/**
 * Pipe that converts a document from an input to an output format.
 *
 * @param <InputDocument>  input document
 * @param <OutputDocument> output document
 * @author ansk98
 */
public interface IDocumentPipe<InputDocument, OutputDocument> {

    /**
     * Pipes an {@link InputDocument} to {@link OutputDocument}.
     *
     * @param inputDocument input document
     * @return output document
     */
    OutputDocument pipe(InputDocument inputDocument);
}
