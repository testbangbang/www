package com.onyx.android.sdk.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Created by zhuzeng on 4/19/16.
 */
public class TexShape extends BaseShape  {


    /**
     * rectangle, circle, etc.
     * @return
     */
    public int getType() {
        return ShapeFactory.SHAPE_TEXT;
    }

    public void render(final RenderContext renderContext) {
        float left = Math.min(getDownPoint().x, getCurrentPoint().x);
        float height = Math.abs(getDownPoint().y - getCurrentPoint().y);
        renderContext.paint.setStyle(Paint.Style.STROKE);
        renderContext.paint.setTextSize(height / 2);
        renderContext.canvas.drawText("Sample text", left, getCurrentPoint().y, renderContext.paint);
    }

}
