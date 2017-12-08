package com.onyx.jdread.library.event;

/**
 * Created by hehai on 17-11-25.
 */

public class LoadingDialogEvent {
    private int resId;

    public LoadingDialogEvent(int resId) {
        this.resId = resId;
    }

    public int getResId() {
        return resId;
    }
}
