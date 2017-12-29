package com.onyx.jdread.shop.event;

import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;

/**
 * Created by jackdeng on 2017/12/20.
 */

public class OnBookDetailReadNowEvent {

    private BookDetailResultBean.Detail bookDetailBean;

    public OnBookDetailReadNowEvent(BookDetailResultBean.Detail bookDetailBean) {
        this.bookDetailBean = bookDetailBean;
    }

    public BookDetailResultBean.Detail getBookDetailBean() {
        return bookDetailBean;
    }
}