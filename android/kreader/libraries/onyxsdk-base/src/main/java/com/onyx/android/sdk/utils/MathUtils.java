package com.onyx.android.sdk.utils;

import android.graphics.PointF;

/**
 * Created by Joy on 2016/6/2.
 */
public class MathUtils {
    public static double distance(int x1, int y1, int x2, int y2) {
        return Math.hypot(Math.abs(x1 - x2), Math.abs(y1 - y2));
    }

    /**
     * p1 means the fix center point, p2 is the extend point from center point ,p3 is the touch point
     *
     * @return
     */
    public static float calculateAngle(PointF center, PointF current, PointF previous) {
        float v1x = current.x - center.x;
        float v1y = current.y - center.y;

        //need to normalize:
        double l1 = Math.sqrt(v1x * v1x + v1y * v1y);
        v1x /= l1;
        v1y /= l1;

        float v2x = previous.x - center.x;
        float v2y = previous.y - center.y;

        //need to normalize:
        double l2 = Math.sqrt(v2x * v2x + v2y * v2y);
        v2x /= l2;
        v2y /= l2;

        double rad = Math.atan2(v2y, v2x) - Math.atan2(v1y, v1x);
        float degrees = (float) Math.toDegrees(rad);
        return degrees > 0 ? degrees : 360f + degrees;
    }

    public static PointF calculateMiddlePointFromTwoPoint(double p1X, double p1Y, double p2X, double p2Y) {
        return new PointF(((float) ((p1X + p2X) / 2)), ((float) (p1Y + p2Y) / 2));
    }
}
