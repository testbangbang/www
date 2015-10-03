package com.onyx.reader.plugin;

/**
 * Created by zhuzeng on 10/2/15.
 */
public interface ReaderDocument {

    /**
     * Read the document metadata.
     * @param metadata
     * @return
     */
    public boolean readMetadata(final ReaderDocumentMetadata metadata);


    /**
     * Read the document table of content.
     * @param toc
     * @return
     */
    public boolean readTableOfContent(final ReaderDocumentTableOfContent toc);

    /**
     * create corresponding view. TODO: support multiple type of view?
     * @return
     */
    public ReaderView createView();


    /**
     * Close the document.
     */
    public void close();


}
