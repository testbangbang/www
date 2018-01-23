package com.onyx.jdread.shop.event;

/**
 * Created by jackdeng on 2018/1/17.
 */

public class BookSearchPathEvent {
    public String catId;

    public BookSearchPathEvent(String catId) {
        this.catId = catId;
    }
}
