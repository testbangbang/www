package com.onyx.jdread.shop.event;

/**
 * Created by jackdeng on 2018/1/17.
 */

public class BookSearchPathEvent {
    public String catId;
    public String catName;

    public BookSearchPathEvent(String catId, String catName) {
        this.catId = catId;
        this.catName = catName;
    }
}
