package com.onyx.android.note.common;

/**
 * Created by lxm on 2018/2/27.
 */

public enum StrokeWidth {

    ULTRA_LIGHT(2.0f), LIGHT(4.0f), NORMAL(6.0f), BOLD(8.0f), ULTRA_BOLD(10.0f);

    StrokeWidth(float width) {
        this.width = width;
    }

    private float width;

    public float getWidth() {
        return width;
    }
}
