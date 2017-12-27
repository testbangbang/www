package com.onyx.android.plato.event;

/**
 * Created by zhouzhiming on 2017/12/27.
 */
public class ExceptionEvent {
    public String error;

    public ExceptionEvent(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
