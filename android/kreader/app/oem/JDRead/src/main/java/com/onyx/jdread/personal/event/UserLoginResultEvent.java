package com.onyx.jdread.personal.event;

/**
 * Created by jackdeng on 2017/12/26.
 */

public class UserLoginResultEvent {
    private String message;

    public UserLoginResultEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
