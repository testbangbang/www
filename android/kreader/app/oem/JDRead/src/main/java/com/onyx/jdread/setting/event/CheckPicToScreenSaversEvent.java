package com.onyx.jdread.setting.event;

/**
 * Created by hehai on 18-1-1.
 */

public class CheckPicToScreenSaversEvent {
    private String path;

    public String getPath() {
        return path;
    }

    public CheckPicToScreenSaversEvent(String path) {
        this.path = path;
    }
}
