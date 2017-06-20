package com.onyx.android.sdk.reader.api;

/**
 * Created by zhuzeng on 10/2/15.
 */
public class ReaderDocumentTableOfContent {

    private ReaderDocumentTableOfContentEntry rootEntry;

    public ReaderDocumentTableOfContent() {
        super();
        rootEntry = new ReaderDocumentTableOfContentEntry();
    }

    public ReaderDocumentTableOfContentEntry getRootEntry() {
        return rootEntry;
    }

    public boolean isEmpty() {
        return rootEntry == null || rootEntry.getChildren() == null || rootEntry.getChildren().size() == 0;
    }
}
