package com.onyx.android.dr.event;

/**
 * Created by zhouzhiming on 2017/7/12.
 */
public class NewWordQueryEvent {
    private final String content;

    public NewWordQueryEvent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
