package com.onyx.download.onyxdownloadservice;

/**
 * Created by li on 2017/8/25.
 */

public class DownloadErrorEvent {
    private String message;

    public DownloadErrorEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
