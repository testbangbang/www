package com.onyx.jdread.reader.event;

/**
 * Created by huxiaomao on 17/11/13.
 */

public class OpenDocumentFailResultEvent {
    private String message;
    private Throwable throwable;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}
