package com.onyx.jdread.shop.event;

/**
 * Created by jackdeng on 2017/12/16.
 */

public class OnBookDetailTopBackEvent {

    private int tag;

    public OnBookDetailTopBackEvent(int tag) {
        this.tag = tag;
    }

    public int getTag() {
        return tag;
    }
}