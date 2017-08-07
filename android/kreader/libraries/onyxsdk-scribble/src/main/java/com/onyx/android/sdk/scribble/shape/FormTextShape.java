package com.onyx.android.sdk.scribble.shape;

import android.graphics.Paint;
import android.graphics.RectF;

import com.onyx.android.sdk.scribble.data.ShapeExtraAttributes;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by ming on 6/22/17.
 */
public class FormTextShape extends BaseShape  {

    public int getType() {
        return ShapeFactory.SHAPE_FORM_TEXT;
    }

    public void render(final RenderContext renderContext) {
        ShapeExtraAttributes shapeExtraAttributes = getShapeExtraAttributes();
        if (shapeExtraAttributes == null) {
            return;
        }
        String content = shapeExtraAttributes.getTextContent();
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

        float textSize = shapeExtraAttributes.getTextSize();
        renderContext.paint.setTextSize(textSize/2);
        Paint.Style beforeStyle = renderContext.paint.getStyle();
        renderContext.paint.setStyle(Paint.Style.FILL_AND_STROKE);
        renderContext.paint.setStrokeWidth(getStrokeWidth());
        renderContext.canvas.drawText(shapeExtraAttributes.getTextContent(), rect.left, rect.top, renderContext.paint);
        renderContext.paint.setStyle(beforeStyle);
    }

}
