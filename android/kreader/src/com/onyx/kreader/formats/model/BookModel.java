package com.onyx.kreader.formats.model;

/**
 * Created by zengzhu on 2/28/16.
 */
public class BookModel {

    private BookContext bookContext;
    private TextModel textModel;


    public BookModel(final String path) {
        bookContext = new BookContext(path);
    }

    public final BookContext getBookContext() {
        return bookContext;
    }
}
