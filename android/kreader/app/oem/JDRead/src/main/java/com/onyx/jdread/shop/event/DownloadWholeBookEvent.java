package com.onyx.jdread.shop.event;

import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;

/**
 * Created by jackdeng on 2017/12/27.
 */

public class DownloadWholeBookEvent {

    private BookDetailResultBean.DetailBean bookDetailBean;

    public DownloadWholeBookEvent(BookDetailResultBean.DetailBean bookDetailBean) {
        this.bookDetailBean = bookDetailBean;
    }

    public BookDetailResultBean.DetailBean getBookDetailBean() {
        return bookDetailBean;
    }
}
