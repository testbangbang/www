package com.onyx.android.sdk.reader.api;

import android.graphics.Bitmap;
import android.graphics.RectF;

import com.onyx.android.sdk.reader.host.options.BaseOptions;

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
     * @param bitmap
     */
    public boolean readCover(final Bitmap bitmap);

    /**
     * Retrieve the page natural size.
     * @param position
     * @return
     */
    public RectF getPageOriginSize(final String position);

    public boolean supportTextPage();

    public boolean isTextPage(final String position);

    /**
     * Retrieve page text
     * @param position
     * @return
     */
    public String getPageText(final String position);

    public ReaderSentence getSentence(final String position, final String sentenceStartPosition);

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
     * read built-in options from document
     * @return false if not exists
     * @param options
     */
    public boolean readBuiltinOptions(BaseOptions options);

    public boolean saveOptions();

    /**
     * Close the document.
     */
    public void close();

    public void updateDocumentOptions(final ReaderDocumentOptions documentOptions, final ReaderPluginOptions pluginOptions);

}
