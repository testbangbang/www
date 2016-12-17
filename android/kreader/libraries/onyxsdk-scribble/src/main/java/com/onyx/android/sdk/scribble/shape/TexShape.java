package com.onyx.android.sdk.scribble.shape;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import com.onyx.android.sdk.scribble.data.ExtraAttributes;
import com.onyx.android.sdk.utils.StringUtils;

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
        ExtraAttributes extraAttributes = getExtraAttributes();
        if (extraAttributes == null) {
            return;
        }
        String content = extraAttributes.getTextContent();
        if (StringUtils.isNullOrEmpty(content)) {
            return;
        }

        float sx = getDownPoint().getX();
        float sy = getDownPoint().getY();
        float ex = getCurrentPoint().getX();
        float ey = getCurrentPoint().getY();
        RectF rect = new RectF(sx, sy, ex, ey);
        if (renderContext.matrix != null) {
            renderContext.matrix.mapRect(rect);
        }

        float textSize = extraAttributes.getTextSize();
        renderContext.paint.setTextSize(textSize);
        renderContext.canvas.drawText(extraAttributes.getTextContent(), rect.left, rect.bottom - (rect.bottom - rect.top) / 2 , renderContext.paint);
    }

}
