package com.onyx.kreader.scribble;


import android.graphics.Matrix;


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


}
