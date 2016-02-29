package com.onyx.kreader.formats.model;

/**
 * Created by zengzhu on 2/28/16.
 */
public interface BookReader {

    /**
     * for async loading, we always use readNext to read next block.
     * @param bookModel the book model.
     * @param context the loading context.
     * @return true if can continue reading more.
     */
    public boolean readNext(final BookModel bookModel, final BookReaderContext context);

}
