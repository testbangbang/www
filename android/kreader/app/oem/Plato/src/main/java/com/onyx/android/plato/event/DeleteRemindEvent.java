package com.onyx.android.plato.event;

/**
 * Created by li on 2017/11/13.
 */

public class DeleteRemindEvent {
    private int remindId;

    public DeleteRemindEvent(int remindId) {
        this.remindId = remindId;
    }

    public int getRemindId() {
        return remindId;
    }
}
