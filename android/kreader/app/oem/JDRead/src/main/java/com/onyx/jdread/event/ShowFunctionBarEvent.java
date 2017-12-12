package com.onyx.jdread.event;

/**
 * Created by hehai on 17-12-12.
 */

public class ShowFunctionBarEvent {
    private boolean show;

    public ShowFunctionBarEvent(boolean show) {
        this.show = show;
    }

    public boolean isShow() {
        return show;
    }
}
