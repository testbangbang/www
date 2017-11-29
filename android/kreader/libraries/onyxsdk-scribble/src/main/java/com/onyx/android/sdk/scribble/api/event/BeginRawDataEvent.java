package com.onyx.android.sdk.scribble.api.event;

import com.onyx.android.sdk.scribble.data.TouchPoint;

/**
 * Created by solskjaer49 on 2017/8/2 17:49.
 */

public class BeginRawDataEvent {
    private boolean shortcutDrawing;
    private TouchPoint point;

    public BeginRawDataEvent(boolean shortcutDrawing, TouchPoint point) {
        this.shortcutDrawing = shortcutDrawing;
        this.point = point;
    }

    public boolean isShortcutDrawing() {
        return shortcutDrawing;
    }

    public TouchPoint getPoint() {
        return point;
    }
}
