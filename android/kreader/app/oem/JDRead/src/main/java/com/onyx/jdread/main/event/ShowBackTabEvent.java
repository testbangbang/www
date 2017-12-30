package com.onyx.jdread.main.event;

/**
 * Created by hehai on 17-12-27.
 */

public class ShowBackTabEvent {
    private boolean show;

    public ShowBackTabEvent(boolean show) {
        this.show = show;
    }

    public boolean isShow() {
        return show;
    }
}
