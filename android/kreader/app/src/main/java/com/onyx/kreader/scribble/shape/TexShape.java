package com.onyx.kreader.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
/**
 * Created by zhuzeng on 4/19/16.
 */
public class TexShape extends NormalShape  {


    /**
     * rectangle, circle, etc.
     * @return
     */
    public int getType() {
        return ShapeFactory.SHAPE_TEXT;
    }

    public void render(final Matrix matrix, final Canvas canvas, final Paint paint) {

        float left = Math.min(getDownPoint().x, getCurrentPoint().x);
        float height = Math.abs(getDownPoint().y - getCurrentPoint().y);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(height / 2);
        canvas.drawText("Sample text", left, getCurrentPoint().y, paint);
    }

}
