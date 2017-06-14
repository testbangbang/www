package com.onyx.edu.reader.ui.events;

/**
 * Created by ming on 2017/6/14.
 */

public class DialogUIChangeEvent {

    private boolean uiOpen;

    public boolean isUiOpen() {
        return uiOpen;
    }

    public void setUiOpen(boolean uiOpen) {
        this.uiOpen = uiOpen;
    }

    public DialogUIChangeEvent(boolean uiOpen) {
        this.uiOpen = uiOpen;
    }

    public static DialogUIChangeEvent create(boolean uiOpen) {
        return new DialogUIChangeEvent(uiOpen);
    }
}
