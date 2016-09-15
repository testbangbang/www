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

    void setDocumentUniqueId(final String documentUniqueId);

    String getDocumentUniqueId();

    void setPageUniqueId(final String pageId);

    String getPageUniqueId();

    void setShapeUniqueId(final String uniqueId);

    String getShapeUniqueId();

    void ensureShapeUniqueId();

    void setPageOriginWidth(int width);

    int getPageOriginWidth();

    void setPageOriginHeight(int height);

    int getPageOriginHeight();

    int getZOrder();

    void setZOrder(int order);

    int getColor();

    void setColor(int color);

    float getStrokeWidth();

    void setStrokeWidth(final float width);

    RectF getBoundingRect();

    void updateBoundingRect();

    void moveTo(final float x, final float y);

    void resize(final float width, final float height);

    int getOrientation();

    void setOrientation(int orientation);

    void onDown(final TouchPoint normalizedPoint, final TouchPoint screenPoint);

    void onMove(final TouchPoint normalizedPoint, final TouchPoint screenPoint);

    void onUp(final TouchPoint normalizedPoint, final TouchPoint screenPoint);

    void addPoints(final TouchPointList points);

    TouchPointList getPoints();

    boolean supportDFB();

    void render(final RenderContext renderContext);

    boolean hitTest(final float x, final float y, final float radius);

    boolean fastHitTest(final float x, final float y, final float radius);

    void clear();

}
