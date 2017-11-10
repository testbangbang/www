package com.onyx.download.onyxdownloadservice;

/**
 * Created by 12 on 2017/3/23.
 */

public class FinishEvent {
    private String path;

    public FinishEvent(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
