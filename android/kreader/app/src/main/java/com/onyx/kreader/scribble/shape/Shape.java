package com.onyx.kreader.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import com.onyx.kreader.scribble.data.TouchPoint;

/**
 * Created by zhuzeng on 4/19/16.
 * create in main thread and calculate render data in background.
 */
public interface Shape {

    public static int STATE_NORMAL      = 0;
    public static int STATE_SELECTED    = 1;
    public static int STATE_MOVING      = 2;
    public static int STATE_RESIZING    = 3;

    /**
     * rectangle, circle, etc.
     * @return
     */
    public int getType();

    /**
     * get instance unique id
     * @return
     */
    public String getUniqueId();

    public int getZOrder();

    public void setZOrder(int order);

    public int getColor();

    public void setColor(int color);

    public float getStrokeWidth();

    public void setStrokeWidth(final float width);

    public RectF getBoundingRect();

    public void moveTo(final float x, final float y);

    public void resize(final float width, final float height);

    public int getOrientation();

    public void setOrientation(int orientation);

    public void onDown(final TouchPoint normalizedPoint, final TouchPoint screenPoint);

    public void onMove(final TouchPoint normalizedPoint, final TouchPoint screenPoint);

    public void onUp(final TouchPoint normalizedPoint, final TouchPoint screenPoint);

    public boolean supportDFB();

    public void render(final Canvas canvas, final Paint paint);

    public boolean hitTest(final float x, final float y);

}
