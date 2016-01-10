package com.onyx.reader.text;

import android.graphics.RectF;

/**
 * Created by zengzhu on 1/10/16.
 */
public class TextLayoutContext {

    private RectF originRect = new RectF();
    private float leftWidth;
    private float leftHeight;

    public void initializeOriginRect(final RectF rect) {
        originRect.set(rect);
        leftWidth = rect.width();
        leftHeight = rect.height();
    }

    public final RectF getOriginRect() {
        return originRect;
    }

    public float addElement(final float width) {
        if (leftWidth < width) {
            leftWidth = 0;
        }
        leftWidth -= width;
        return leftWidth;
    }

    public float addLine(final float height) {
        leftHeight -= height;
        leftWidth = originRect.width();
        return leftHeight;
    }

    public float getLeftWidth() {
        return leftWidth;
    }

    public float getLeftHeight() {
        return leftHeight;
    }
}
