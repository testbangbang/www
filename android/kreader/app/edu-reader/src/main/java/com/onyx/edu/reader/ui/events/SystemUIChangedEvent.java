package com.onyx.edu.reader.ui.events;

/**
 * Created by zhuzeng on 10/13/16.
 */

public class SystemUIChangedEvent {

    private boolean uiOpen;

    public SystemUIChangedEvent(boolean open) {
        uiOpen = open;
    }

    public boolean isUiOpen() {
        return uiOpen;
    }
}
