package com.onyx.reader.text;

import android.graphics.Paint;

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
}
