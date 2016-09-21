package com.onyx.kreader.ui.events;

/**
 * Created by zhuzeng on 9/20/16.
 */
public class BeforeDocumentOpen {

    private String path;

    public BeforeDocumentOpen(final String p) {
        path = p;
    }

    public final String getPath() {
        return path;
    }


}
