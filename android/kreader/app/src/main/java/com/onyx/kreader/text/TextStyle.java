package com.onyx.kreader.text;

import android.graphics.Paint;
import android.graphics.Rect;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zengzhu on 1/10/16.
 */
public class TextStyle implements Style {

    private Paint paint;
    private static Map<String, Float> widthMap = new HashMap<String, Float>();

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
        if (widthMap.containsKey(text)) {
            return widthMap.get(text);
        }
        float width = getPaint().measureText(text);
        widthMap.put(text, width);
        return width;
    }

    public float measureHeight(final String text) {
        Rect rect = new Rect();
        getPaint().getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }

}
