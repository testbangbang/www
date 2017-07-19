package com.onyx.android.dr.reader.event;

/**
 * Created by hehai on 17-6-6.
 */

public class DisplayStatusBarEvent {
    private boolean isDisplay;

    public DisplayStatusBarEvent(boolean isDisplay) {
        this.isDisplay = isDisplay;
    }

    public boolean isDisplay() {
        return isDisplay;
    }
}
