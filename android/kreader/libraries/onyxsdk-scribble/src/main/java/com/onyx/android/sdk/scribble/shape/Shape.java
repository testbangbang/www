package com.onyx.android.sdk.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;

/**
 * Created by zhuzeng on 4/19/16.
 * create in main thread and calculate render data in background.
 */
public interface Shape {

    static int STATE_NORMAL      = 0;
    static int STATE_SELECTED    = 1;
    static int STATE_MOVING      = 2;
    static int STATE_RESIZING    = 3;

    /**
     * rectangle, circle, etc.
     * @return
     */
    int getType();

    void setUniqueId(final String uniqueId);

    String getUniqueId();

    int getZOrder();

    void setZOrder(int order);

    int getColor();

    void setColor(int color);

    float getStrokeWidth();

    void setStrokeWidth(final float width);

    RectF getBoundingRect();

    void moveTo(final float x, final float y);

    void resize(final float width, final float height);

    int getOrientation();

    void setOrientation(int orientation);

    void onDown(final TouchPoint normalizedPoint, final TouchPoint screenPoint);

    void onMove(final TouchPoint normalizedPoint, final TouchPoint screenPoint);

    void onUp(final TouchPoint normalizedPoint, final TouchPoint screenPoint);

    void addPoints(final TouchPointList points);

    boolean supportDFB();

    void render(final Matrix matrix, final Canvas canvas, final Paint paint);

    boolean hitTest(final float x, final float y);

}
