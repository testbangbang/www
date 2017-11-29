package com.onyx.android.sdk.scribble.shape;

import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.scribble.EPDRenderer;
import com.onyx.android.sdk.scribble.data.TouchPoint;

/**
 * Created by zhuzeng on 4/26/16.
 */
public class EPDShape extends BaseShape {


    private static final String TAG = EPDShape.class.getSimpleName();
    public static final UpdateMode updateMode = UpdateMode.DU;
    public static int PEN_NULL = 0;
    public static int PEN_DOWN = 1;
    public static int PEN_MOVE = 2;
    public static int PEN_UP = 3;
    private int penState = PEN_NULL;


    public boolean supportDFB() {
        return true;
    }

    public void onDown(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        setPenDown();
        super.onDown(normalizedPoint, screenPoint);
        if (!useRawInput()) {
            EPDRenderer.moveTo(screenPoint.x, screenPoint.y, getDisplayStrokeWidth());
        }
    }

    public void onMove(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        if (isPenNull()) {
            onDown(normalizedPoint, screenPoint);
        }
        setPenMove();
        super.onMove(normalizedPoint, screenPoint);
        if (!useRawInput()) {
            EPDRenderer.quadTo(screenPoint.x, screenPoint.y, updateMode);
        }
    }

    public void onUp(final TouchPoint normalizedPoint, final TouchPoint screenPoint) {
        if (isPenMove()) {
            super.onUp(normalizedPoint, screenPoint);
            if (!useRawInput()) {
                EPDRenderer.quadTo(screenPoint.x, screenPoint.y, updateMode);
            }
        }
        setPenNull();
    }

    public void render(final RenderContext renderContext) {
    }

    public void setPenNull() {
        penState = PEN_NULL;
    }

    public void setPenDown() {
        penState = PEN_DOWN;
    }

    public void setPenMove() {
        penState = PEN_MOVE;
    }

    public boolean isPenNull() {
        return penState == PEN_NULL;
    }

    public boolean isPenMove() {
        return penState == PEN_MOVE;
    }


}
