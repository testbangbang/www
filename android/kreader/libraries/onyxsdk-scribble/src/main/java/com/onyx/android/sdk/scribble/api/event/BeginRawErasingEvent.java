package com.onyx.android.sdk.scribble.api.event;

import com.onyx.android.sdk.scribble.data.TouchPoint;

/**
 * Created by solskjaer49 on 2017/8/2 18:13.
 */

public class BeginRawErasingEvent {
    private boolean shortcutDrawing;
    private TouchPoint point;

    public BeginRawErasingEvent(boolean shortcutDrawing, TouchPoint point) {
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
