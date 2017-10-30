package com.onyx.android.dr.reader.event;

/**
 * Created by zhuzeng on 7/29/16.
 */
public class ForceCloseEvent {
    public boolean byUser = false;

    public ForceCloseEvent() {

    }

    public ForceCloseEvent(boolean byUser) {
        this.byUser = byUser;
    }
}
