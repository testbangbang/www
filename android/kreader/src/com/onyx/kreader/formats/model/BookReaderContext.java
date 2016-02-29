package com.onyx.kreader.formats.model;

import com.onyx.kreader.formats.filesystem.BookNIO;

/**
 * Created by zengzhu on 2/28/16.
 */
public class BookReaderContext {

    public BookNIO bookStream;

    public BookReaderContext(final String path) {
        bookStream = new BookNIO(path);
    }



}
