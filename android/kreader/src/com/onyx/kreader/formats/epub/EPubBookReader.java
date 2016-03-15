package com.onyx.kreader.formats.epub;

import com.onyx.kreader.formats.model.BookModel;
import com.onyx.kreader.formats.model.BookReader;

/**
 * Created by zengzhu on 3/15/16.
 */
public class EPubBookReader implements BookReader {

    public boolean open(final BookModel bookModel) {
        return false;
    }

    public boolean processNext(final BookModel bookModel) {
        return false;
    }

    public boolean close(final BookModel bookModel) {
        return false;
    }

}
