package com.onyx.edu.reader.ui.events.im;

import com.onyx.android.sdk.im.data.Message;

/**
 * Created by lxm on 2017/8/10.
 */

public class EmitMessageEvent {

    private Message message;

    public EmitMessageEvent(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public static EmitMessageEvent create(Message message) {
        return new EmitMessageEvent(message);
    }
}
