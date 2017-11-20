package com.onyx.creama.note.event;

/**
 * Created by solskjaer49 on 2017/11/18 16:48.
 */

public class ChangePenWidthEvent {
    private int width = 0;

    public ChangePenWidthEvent(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }
}
