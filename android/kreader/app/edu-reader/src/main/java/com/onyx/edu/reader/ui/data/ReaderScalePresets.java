package com.onyx.edu.reader.ui.data;

/**
 * Created by Joy on 2016/4/21.
 */
public class ReaderScalePresets {
    private static final float MIN_VALUE = 0.1f;
    private static final float DELTA = 0.25f;

    public static float scaleUp(float currentValue) {
        return currentValue + DELTA;
    }

    public static float scaleDown(float currentValue) {
        return Math.max(MIN_VALUE, currentValue - DELTA);
    }
}
