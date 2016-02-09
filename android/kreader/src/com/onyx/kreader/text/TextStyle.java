package com.onyx.kreader.text;

import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by zengzhu on 1/10/16.
 */
public class TextStyle implements Style {

    private Paint paint;

    static public TextStyle create(final Paint p) {
        TextStyle style = new TextStyle();
        style.paint = p;
        return style;
    }

    @Override
    public Paint getPaint() {
        return paint;
    }

    public float measureWidth(final String text) {
        return getPaint().measureText(text);
    }

    public float measureHeight(final String text) {
        Rect rect = new Rect();
        getPaint().getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }

}
