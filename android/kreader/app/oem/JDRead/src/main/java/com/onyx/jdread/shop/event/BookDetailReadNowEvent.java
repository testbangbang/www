package com.onyx.jdread.shop.event;

import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;

/**
 * Created by jackdeng on 2017/12/20.
 */

public class BookDetailReadNowEvent {

    private BookDetailResultBean.DetailBean bookDetailBean;

    public BookDetailReadNowEvent(BookDetailResultBean.DetailBean bookDetailBean) {
        this.bookDetailBean = bookDetailBean;
    }

    public BookDetailResultBean.DetailBean getBookDetailBean() {
        return bookDetailBean;
    }
}