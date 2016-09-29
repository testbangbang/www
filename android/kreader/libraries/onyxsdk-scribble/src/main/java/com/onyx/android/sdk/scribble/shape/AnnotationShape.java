package com.onyx.android.sdk.scribble.shape;

import android.graphics.RectF;

/**
 * Created by zhuzeng on 9/29/16.
 */

public class AnnotationShape extends BaseShape {

    public int getType() {
        return ShapeFactory.SHAPE_ANNOTATION;
    }

    public boolean addMovePoint() {
        return false;
    }

    public boolean hitTest(final float x, final float y, final float radius) {
        return false;
    }

    public void render(final RenderContext renderContext) {
        RectF rect = new RectF(getDownPoint().x,
                getDownPoint().y,
                getCurrentPoint().x,
                getCurrentPoint().y);

        applyStrokeStyle(renderContext.paint);
        if (renderContext.matrix != null) {
            renderContext.matrix.mapRect(rect);
        }
        renderContext.canvas.drawRect(rect, renderContext.paint);
    }

}
