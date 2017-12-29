package com.onyx.jdread.shop.event;


/**
 * Created by jackdeng on 2017/12/21.
 */

public class DownloadFinishEvent {
    public Object tag;
    public DownloadFinishEvent(Object tag) {
        this.tag = tag;
    }
}
