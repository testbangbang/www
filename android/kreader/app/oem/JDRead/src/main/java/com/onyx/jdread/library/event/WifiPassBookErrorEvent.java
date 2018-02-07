package com.onyx.jdread.library.event;

/**
 * Created by hehai on 18-2-7.
 */

public class WifiPassBookErrorEvent {
    private String message;

    public WifiPassBookErrorEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
