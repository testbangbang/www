package com.onyx.jdread.shop.event;

import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;

/**
 * Created by jackdeng on 2017/12/20.
 */

public class BookDetailReadNowEvent {

    private BookDetailResultBean bookDetailResultBean;

    public BookDetailReadNowEvent(BookDetailResultBean bookDetailResultBean) {
        this.bookDetailResultBean = bookDetailResultBean;
    }

    public BookDetailResultBean getBookDetailResultBean() {
        return bookDetailResultBean;
    }
}