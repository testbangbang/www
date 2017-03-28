package com.onyx.kreader.ui.events;

/**
 * Created by zhuzeng on 04/03/2017.
 */

public class DocumentActivatedEvent {

    private String activeDocPath;

    public DocumentActivatedEvent(final String path) {
        activeDocPath = path;
    }

    public String getActiveDocPath() {
        return activeDocPath;
    }

    public void setActiveDocPath(String activeDocPath) {
        this.activeDocPath = activeDocPath;
    }
}
