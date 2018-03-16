package com.onyx.jdread.personal.event;

/**
 * Created by jackdeng on 2017/12/26.
 */

public class UserLoginResultEvent {
    private String message;
    private String targetView;
    private int resultCode;

    public UserLoginResultEvent(String message) {
        this.message = message;
    }

    public UserLoginResultEvent(String message, int resultCode) {
        this.message = message;
        this.resultCode = resultCode;
    }

    public UserLoginResultEvent(String message, String targetView) {
        this.message = message;
        this.targetView = targetView;
    }

    public String getMessage() {
        return message;
    }

    public String getTargetView() {
        return targetView;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }
}
