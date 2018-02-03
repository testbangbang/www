package com.onyx.jdread.library.event;

import com.onyx.jdread.main.common.ResManager;

/**
 * Created by hehai on 17-11-25.
 */

public class LoadingDialogEvent {
    private String message;

    public LoadingDialogEvent(int resId) {
        message = ResManager.getString(resId);
    }

    public LoadingDialogEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
