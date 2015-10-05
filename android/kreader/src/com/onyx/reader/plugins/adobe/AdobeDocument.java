package com.onyx.reader.plugins.adobe;

import android.graphics.RectF;
import com.onyx.reader.api.*;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class AdobeDocument implements ReaderDocument {

    private AdobeReaderPlugin parent;

    public AdobeDocument(final AdobeReaderPlugin p) {
        parent = p;
    }

    public boolean readMetadata(final ReaderDocumentMetadata metadata) {
        return false;
    }

    public boolean readCover(final ReaderBitmap bitmap) {
        return false;
    }

    public RectF getPageOriginalSize(final ReaderDocumentPosition position) {
        return null;
    }

    public boolean readTableOfContent(final ReaderDocumentTableOfContent toc) {
        return false;
    }

    public ReaderView createView(final ReaderViewOptions viewOptions) {
        return null;
    }

    public void close() {

    }

    private AdobePluginImpl getPluginImpl() {
        return parent.getPluginImpl();
    }

}
