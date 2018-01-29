package com.onyx.jdread.personal.event;

/**
 * Created by li on 2018/1/29.
 */

public class RequestFailedEvent {
    private String message;

    public RequestFailedEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
