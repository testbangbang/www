package com.onyx.edu.reader.ui.events;

/**
 * Created by zhuzeng on 30/03/2017.
 */

public class ConfirmCloseDialogEvent {
    private boolean open;

    public ConfirmCloseDialogEvent(boolean o) {
        open = o;
    }

    public boolean isOpen() {
        return open;
    }

}
