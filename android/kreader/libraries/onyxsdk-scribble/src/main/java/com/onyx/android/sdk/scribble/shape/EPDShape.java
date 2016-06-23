package com.onyx.android.sdk.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.scribble.EPDRenderer;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;

/**
 * Created by zhuzeng on 4/26/16.
 */
public class EPDShape extends BaseShape {

    static public final UpdateMode updateMode = UpdateMode.DU;

    /**
     * rectangle, circle, etc.
     * @return
     */
    public int getType() {
        return ShapeFactory.SHAPE_NORMAL_SCRIBBLE;
    }

    public boolean supportDFB() {
        return true;
    }

    public void onDown(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        super.onDown(normalizedPoint, screenPoint);
        EPDRenderer.moveTo(screenPoint.x, screenPoint.y, getStrokeWidth());
    }

    public void onMove(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        super.onMove(normalizedPoint, screenPoint);
        EPDRenderer.quadTo(screenPoint.x, screenPoint.y, updateMode);
    }

    public void onUp(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        super.onUp(normalizedPoint, screenPoint);
        EPDRenderer.quadTo(screenPoint.x, screenPoint.y, updateMode);
    }

    public void render(final Matrix matrix, final Canvas canvas, final Paint paint) {
    }

    /**
     * check with normalized point.
     * @param x
     * @param y
     * @return
     */
    public boolean hitTest(final float x, final float y) {
        if (!getBoundingRect().contains(x, y)) {
            return false;
        }
        return true;
    }
}
