package com.onyx.edu.reader.ui.events;

/**
 * Created by zhuzeng on 9/20/16.
 */
public class BeforeDocumentOpenEvent {

    private String path;

    public BeforeDocumentOpenEvent(final String p) {
        path = p;
    }

    public final String getPath() {
        return path;
    }


}
