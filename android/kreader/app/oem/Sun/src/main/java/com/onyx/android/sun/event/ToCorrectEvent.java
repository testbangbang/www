package com.onyx.android.sun.event;

/**
 * Created by li on 2017/10/16.
 */

public class ToCorrectEvent {
    private boolean hasCorrected;

    public ToCorrectEvent(boolean hasCorrected) {
        this.hasCorrected = hasCorrected;
    }

    public boolean isHasCorrected() {
        return hasCorrected;
    }
}
