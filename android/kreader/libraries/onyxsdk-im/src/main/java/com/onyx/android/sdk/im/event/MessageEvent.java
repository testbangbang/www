package com.onyx.android.sdk.im.event;

import com.onyx.android.sdk.im.data.Message;

/**
 * Created by ming on 2017/7/14.
 */

public class MessageEvent {

    public Message message;

    public MessageEvent(Message message) {
        this.message = message;
    }

    public static MessageEvent create(Message message) {
        return new MessageEvent(message);
    }
}
