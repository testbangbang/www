package com.onyx.jdread.shop.event;


/**
 * Created by jackdeng on 2017/12/21.
 */

public class DownloadStartEvent {
    public Object tag;
    public DownloadStartEvent(Object tag) {
        this.tag = tag;
    }
}
