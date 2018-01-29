package com.onyx.jdread.personal.event;

/**
 * Created by li on 2018/1/27.
 */

public class VerifySignEvent {
    private boolean todaySign;

    public VerifySignEvent(boolean todaySign) {
        this.todaySign = todaySign;
    }

    public boolean isTodaySign() {
        return todaySign;
    }
}
