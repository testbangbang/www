package com.onyx.android.sdk.scribble.math;


import android.graphics.Rect;
import com.onyx.android.sdk.scribble.data.TouchPoint;

/**
 * Created by zhuzeng on 10/2/15.
 */
public class OnyxMatrix {

    static private abstract class OnyxMatrixMap {
        public void mapPoints(final float dst[], final float src[], final int dx, final int dy) {

        }
    }

    static private class OnyxMatrixMap0 extends OnyxMatrixMap  {
        public void mapPoints(final float dst[], final float src[], final int dx, final int dy) {
            dst[0] = dx + src[0];
            dst[1] = src[1];
        }
    }

    static private class OnyxMatrixMap90 extends OnyxMatrixMap  {
        public void mapPoints(final float dst[], final float src[], final int dx, final int dy) {
            dst[0] = dx - src[1];
            dst[1] = src[0];
        }
    }

    static private class OnyxMatrixMap180 extends OnyxMatrixMap  {
        public void mapPoints(final float dst[], final float src[], final int dx, final int dy) {
            dst[0] = dx - src[0];
            dst[1] = -src[1];
        }
    }

    static private class OnyxMatrixMap270 extends OnyxMatrixMap  {
        public void mapPoints(final float dst[], final float src[], final int dx, final int dy) {
            dst[0] = src[1];
            dst[1] = dy - src[0];
        }
    }

    private int rotate = 0;
    private int dx = 0;
    private int dy = 0;
    private OnyxMatrixMap impl;
    private float src[] = new float[2];
    private float dst[] = new float[2];

    public void postRotate(final int degree) {
        rotate = degree;
        if (rotate == 0) {
            impl = new OnyxMatrixMap0();
        } else if (rotate == 90) {
            impl = new OnyxMatrixMap90();
        } else if (rotate == 180) {
            impl = new OnyxMatrixMap180();
        } else if (rotate == 270) {
            impl = new OnyxMatrixMap270();
        }
    }

    public void postTranslate(final int tx, final int ty) {
        dx = tx;
        dy = ty;
    }

    public void mapPoints(final float dst[], final float src[]) {
        impl.mapPoints(dst, src, dx, dy);
    }

    public Rect mapInPlace(final Rect origin) {
        float dstB[] = new float[2];
        src[0] = origin.left;
        src[1] = origin.top;
        mapPoints(dst, src);

        src[0] = origin.right;
        src[1] = origin.bottom;
        mapPoints(dstB, src);
        origin.set((int) dst[0], (int) dst[1], (int) dstB[0], (int) dstB[1]);
        return origin;
    }

    public TouchPoint map(final TouchPoint touchPoint) {
        src[0] = touchPoint.getX();
        src[1] = touchPoint.getY();
        mapPoints(dst, src);
        TouchPoint result = new TouchPoint(dst[0], dst[1], touchPoint.getPressure(), touchPoint.getSize(), touchPoint.getTimestamp());
        return result;
    }

}
