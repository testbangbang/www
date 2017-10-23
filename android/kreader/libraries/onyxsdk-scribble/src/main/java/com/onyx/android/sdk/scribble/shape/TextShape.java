package com.onyx.android.sdk.scribble.shape;

import android.graphics.Paint;
import android.graphics.RectF;

import com.onyx.android.sdk.scribble.data.ShapeExtraAttributes;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by zhuzeng on 4/19/16.
 */
public class TextShape extends BaseShape  {

    /**
     * rectangle, circle, etc.
     * @return
     */
    public int getType() {
        return ShapeFactory.SHAPE_TEXT;
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
        renderContext.paint.setTextSize(textSize);
        Paint.Style beforeStyle = renderContext.paint.getStyle();
        renderContext.paint.setStyle(Paint.Style.FILL_AND_STROKE);
        renderContext.paint.setStrokeWidth(getStrokeWidth());
        float baseline;
        if (StringUtils.isChinese(content)) {
            Paint.FontMetricsInt fontMetrics = renderContext.paint.getFontMetricsInt();
            baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        }else {
            baseline = rect.bottom;
        }
        renderContext.canvas.drawText(content, rect.left, baseline, renderContext.paint);
        renderContext.paint.setStyle(beforeStyle);
    }

}
