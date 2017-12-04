package com.onyx.android.dr.event;

/**
 * Created by zhouzhiming on 2017/12/4.
 */
public class OpenDialogEvent {
    public String content;

    public  OpenDialogEvent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
