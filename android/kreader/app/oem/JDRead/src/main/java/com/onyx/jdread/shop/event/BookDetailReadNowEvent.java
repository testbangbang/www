package com.onyx.jdread.shop.event;

import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;

/**
 * Created by jackdeng on 2017/12/20.
 */

public class BookDetailReadNowEvent {

    private BookDetailResultBean.Detail bookDetailBean;

    public BookDetailReadNowEvent(BookDetailResultBean.Detail bookDetailBean) {
        this.bookDetailBean = bookDetailBean;
    }

    public BookDetailResultBean.Detail getBookDetailBean() {
        return bookDetailBean;
    }
}