package com.onyx.kreader.api;

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

}
