package com.onyx.android.dr.event;

/**
 * Created by zhouzhiming on 2017/10/18.
 */
public class SignUpEvent {
    private String message;

    public SignUpEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
