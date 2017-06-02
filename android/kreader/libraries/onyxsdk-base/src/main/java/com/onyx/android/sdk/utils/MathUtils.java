package com.onyx.android.sdk.utils;

/**
 * Created by Joy on 2016/6/2.
 */
public class MathUtils {
    public static double distance(int x1, int y1, int x2, int y2) {
        return Math.hypot(Math.abs(x1 - x2), Math.abs(y1 - y2));
    }
}
