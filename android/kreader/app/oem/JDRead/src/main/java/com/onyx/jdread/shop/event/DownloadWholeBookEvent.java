package com.onyx.jdread.shop.event;

import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;

/**
 * Created by jackdeng on 2017/12/27.
 */

public class DownloadWholeBookEvent {

    private BookDetailResultBean bookDetailResultBean;

    public DownloadWholeBookEvent(BookDetailResultBean bookDetailResultBean) {
        this.bookDetailResultBean = bookDetailResultBean;
    }

    public BookDetailResultBean getBookDetailResultBean() {
        return bookDetailResultBean;
    }
}
