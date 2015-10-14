package com.onyx.reader.api;

import android.graphics.RectF;

/**
 * Created by zhuzeng on 10/2/15.
 */
public interface ReaderDocument {

    /**
     * Read the document metadata.
     * @param metadata The metadata interface.
     * @return
     */
    public boolean readMetadata(final ReaderDocumentMetadata metadata);

    /**
     * Retrieve cover image.
     */
    public boolean readCover(final ReaderBitmap bitmap);

    /**
     * Retrieve the page natural size.
     * @param position
     * @return
     */
    public RectF getPageNaturalSize(final ReaderDocumentPosition position);

    /**
     * Read the document table of content.
     * @param toc
     * @return
     */
    public boolean readTableOfContent(final ReaderDocumentTableOfContent toc);

    /**
     * create corresponding view.
     * @param viewOptions The view options.
     * @return The created view. null if failed.
     */
    public ReaderView createView(final ReaderViewOptions viewOptions);

    /**
     * Close the document.
     */
    public void close();


}
