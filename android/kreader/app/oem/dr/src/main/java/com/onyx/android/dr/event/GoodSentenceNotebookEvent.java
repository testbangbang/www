package com.onyx.android.dr.event;

/**
 * Created by zhouzhiming on 2017/7/11.
 */
public class GoodSentenceNotebookEvent {
    private final String content;

    public GoodSentenceNotebookEvent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
