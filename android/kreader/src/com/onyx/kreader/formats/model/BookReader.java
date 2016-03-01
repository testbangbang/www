package com.onyx.kreader.formats.model;

/**
 * Created by zengzhu on 2/28/16.
 */
public interface BookReader {

    /**
     * open book and prepare for reading data.
     * @param bookModel the BookModel object.
     * @return
     */
    public boolean open(final BookModel bookModel);

    /**
     * for async loading, we always use processNext to read next block.
     * @param bookModel the book model.
     * @return true if can continue reading more.
     */
    public boolean processNext(final BookModel bookModel);

}
