package com.onyx.kreader.ui.events;

/**
 * Created by zhuzeng on 7/29/16.
 */
public class DocumentOpenEvent {
    private String path;

    public DocumentOpenEvent(final String p) {
        path = p;
    }

    public final String getPath() {
        return path;
    }
}
