package com.onyx.jdread.shop.event;


/**
 * Created by jackdeng on 2017/12/21.
 */

public class DownloadFinishEvent {
    public Throwable throwable;
    public Object tag;
    public DownloadFinishEvent(Object tag) {
        this.tag = tag;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}
