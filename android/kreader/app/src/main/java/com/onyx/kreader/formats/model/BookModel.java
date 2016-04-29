package com.onyx.kreader.formats.model;

/**
 * Created by zengzhu on 2/28/16.
 * including text model, toc tree and other resources.
 */
public class BookModel {

    private BookMetadata bookMetadata;
    private TextModel textModel;

    public BookModel() {
    }

    public final TextModel getTextModel() {
        if (textModel == null) {
            textModel = new TextModel();
        }
        return textModel;
    }

    public final BookMetadata getBookMetadata() {
        if (bookMetadata == null) {
            bookMetadata = new BookMetadata();
        }
        return bookMetadata;
    }
}
