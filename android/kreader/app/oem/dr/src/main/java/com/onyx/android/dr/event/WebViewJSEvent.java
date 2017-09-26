package com.onyx.android.dr.event;

/**
 * Created by li on 2017/9/26.
 */

public class WebViewJSEvent {
    private String message;

    public WebViewJSEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
