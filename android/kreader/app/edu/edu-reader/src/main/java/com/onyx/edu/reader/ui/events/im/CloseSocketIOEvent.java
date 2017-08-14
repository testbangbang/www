package com.onyx.edu.reader.ui.events.im;

/**
 * Created by lxm on 2017/8/10.
 */

public class CloseSocketIOEvent {

    private boolean unRegister = false;

    public CloseSocketIOEvent(boolean unRegister) {
        this.unRegister = unRegister;
    }

    public boolean isUnRegister() {
        return unRegister;
    }

    public static CloseSocketIOEvent create(boolean unRegister) {
        return new CloseSocketIOEvent(unRegister);
    }
}
