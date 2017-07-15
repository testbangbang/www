package com.onyx.android.sdk.im;

/**
 * Created by ming on 2017/7/14.
 */

public class MessageEvent {

    private Message message;

    public MessageEvent(Message message) {
        this.message = message;
    }

    public static MessageEvent create(Message message) {
        return new MessageEvent(message);
    }
}
