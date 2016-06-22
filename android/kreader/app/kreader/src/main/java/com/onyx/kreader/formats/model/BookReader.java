package com.onyx.kreader.formats.model;

/**
 * Created by zengzhu on 2/28/16.
 */
public interface BookReader {

    /**
     * open book and prepare for reading data.
     * @param path the path to read
     * @param bookModel the BookModel object.
     * @return
     */
    public boolean open(final BookReaderContext context, final BookModel bookModel);

    /**
     * for async loading, we always use processNext to read next block.
     * @param bookModel the book model.
     * @return true if can continue reading more.
     */
    public boolean processNext(final BookModel bookModel);

    /**
     * close model.
     * @param bookModel the book model.
     * @return true if model has been successfully closed.
     */
    public boolean close(final BookModel bookModel);

}
