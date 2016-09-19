package com.onyx.android.sdk.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.scribble.EPDRenderer;
import com.onyx.android.sdk.scribble.data.TouchPoint;

/**
 * Created by zhuzeng on 4/26/16.
 */
public class EPDShape extends BaseShape {


    private static final String TAG = EPDShape.class.getSimpleName();
    public static final UpdateMode updateMode = UpdateMode.DU;

    /**
     * rectangle, circle, etc.
     * @return
     */
    public int getType() {
        return ShapeFactory.SHAPE_PENCIL_SCRIBBLE;
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

    public void render(final RenderContext renderContext) {
    }

}
