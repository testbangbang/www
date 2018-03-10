package com.onyx.jdread.shop.event;

/**
 * Created by jackdeng on 2018/1/8.
 */

public class BuyBookSuccessEvent {
    public String buiedBookIds;
    public boolean isNetBook;

    public BuyBookSuccessEvent(String buiedBookIds, boolean isNetBook) {
        this.buiedBookIds = buiedBookIds;
        this.isNetBook = isNetBook;
    }
}
