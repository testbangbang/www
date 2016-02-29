package com.onyx.kreader.formats.model;

import com.onyx.kreader.formats.filesystem.FileNIO;

/**
 * Created by zengzhu on 2/28/16.
 */
public class BookReaderContext {

    public FileNIO bookStream;

    public BookReaderContext(final String path) {
        bookStream = new FileNIO(path);
    }



}
