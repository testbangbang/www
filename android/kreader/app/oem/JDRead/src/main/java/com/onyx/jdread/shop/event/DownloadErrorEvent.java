package com.onyx.jdread.shop.event;


/**
 * Created by jackdeng on 2017/12/21.
 */

public class DownloadErrorEvent {
    public Object tag;
    public DownloadErrorEvent(Object tag) {
        this.tag = tag;
    }
}
