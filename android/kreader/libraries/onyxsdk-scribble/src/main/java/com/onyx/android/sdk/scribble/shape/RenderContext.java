package com.onyx.android.sdk.scribble.shape;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import com.hanvon.core.Algorithm;

/**
 * Created by zhuzeng on 9/15/16.
 */
public class RenderContext {

    public Bitmap bitmap;
    public Canvas canvas;
    public Paint paint;
    public Matrix matrix;
    public boolean force = false;
    public boolean useExternal = false;
    public float handlerSize = 5;
    public float displayScale = 1.0f;

    public void prepareRenderingBuffer(final Bitmap bitmap) {
        if (!useExternal) {
            return;
        }
        Algorithm.initializeEx(bitmap.getWidth(), bitmap.getHeight(), bitmap);
    }

    public void flushRenderingBuffer(final Bitmap bitmap) {
        if (!useExternal) {
            return;
        }
    }

    public RenderContext() {
    }

    public RenderContext(final Canvas c, final Paint p, final Matrix m) {
        canvas = c;
        paint = p;
        matrix = m;
        updateDisplayScale(m);
    }

    public RenderContext(final Bitmap b, final Canvas c, final Paint p, final Matrix m) {
        bitmap = b;
        canvas = c;
        paint = p;
        matrix = m;
        updateDisplayScale(m);
    }

    public static RenderContext create(final Canvas c, final Paint p, final Matrix m) {
        return new RenderContext(c, p, m);
    }

    public static RenderContext create(final Bitmap b, final Canvas c, final Paint p, final Matrix m) {
        return new RenderContext(b, c, p, m);
    }

    public void update(final Bitmap b, final Canvas c, final Paint p, final Matrix m) {
        bitmap = b;
        canvas = c;
        paint = p;
        matrix = m;
        updateDisplayScale(m);
    }

    public void update(final Matrix m) {
        matrix = m;
        updateDisplayScale(m);
    }

    public void update(final Bitmap b, final Canvas c, final Paint p) {
        bitmap = b;
        canvas = c;
        paint = p;
    }

    private void updateDisplayScale(final Matrix m) {
        if (m == null) {
            return;
        }
        float[] f = new float[9];
        m.getValues(f);
        displayScale = f[Matrix.MSCALE_X];
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
        updateDisplayScale(matrix);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
