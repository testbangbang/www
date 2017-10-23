package com.onyx.android.dr.event;

/**
 * Created by zhouzhiming on 2017/10/18.
 */
public class GetVerificationCodeFailedEvent {
    private String message;

    public GetVerificationCodeFailedEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
