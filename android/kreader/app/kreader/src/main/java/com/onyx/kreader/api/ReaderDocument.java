package com.onyx.kreader.api;

import android.graphics.RectF;
import com.onyx.android.sdk.api.ReaderBitmap;

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
    public RectF getPageOriginSize(final String position);

    /**
     * Retrieve page text
     * @param position
     * @return
     */
    public String getPageText(final String position);

    /**
     * Read the document table of content.
     * @param toc
     * @return
     */
    public boolean readTableOfContent(final ReaderDocumentTableOfContent toc);

    /**
     * Get corresponding view.
     * @param viewOptions The view options.
     * @return The created view. null if failed.
     */
    public ReaderView getView(final ReaderViewOptions viewOptions);

    /**
     * Close the document.
     */
    public void close();


}
