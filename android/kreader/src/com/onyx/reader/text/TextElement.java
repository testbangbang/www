package com.onyx.reader.text;

import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zengzhu on 1/9/16.
 */
public class TextElement implements Element {

    private Style style;
    private String text;
    private float x;
    private float y;
    private float width;
    private float height;
    private Rect rect = new Rect();
    static List<String> puncation = Arrays.asList(new String[]{"，", "？", "。", "”", "“", "！", "·", "\""});

    public static TextElement create(final String string, final Style styleRef) {
        TextElement textElement = new TextElement();
        textElement.style = styleRef;
        textElement.text = string;
        return textElement;
    }

    public TextElement() {
    }

    public int type() {
        return 0;
    }

    public float measureWidth() {
        if (width <= 0) {
            width = style.measureWidth(text);
        }
        return width;
    }

    public float spacing() {
        return style.getPaint().measureText(" ");
    }

    public float measureHeight() {
        if (height <= 0) {
            height = style.measureHeight(text);
        }
        return height;
    }

    public boolean layout(final TextLayoutContext textLayoutContext) {
        if (measureWidth() < textLayoutContext.getAvailableWidth() && measureHeight() < textLayoutContext.getAvailableHeight()) {
            return true;
        }
        return false;
    }

    public float baseLine() {
        return measureHeight();
    }

    public boolean canScale() {
        return false;
    }

    public boolean canBeLayoutedAtLineBegin() {
        for(String s : puncation) {
            if (s.contains(text)) {
                return false;
            }
        }
        return true;
    }

    public List<Element> breakElement(final float leftWidth, final float additionalWidth) {
        return null;
    }

    public Style style() {
        return style;
    }

    public void setElementX(final float px) {
        x = px;
    }

    public void setElementY(final float py) {
        y = py;
    }

    public void setElementPosition(final float px, final float py) {
        x = px;
        y = py;
    }

    public void draw(final Canvas canvas) {
        canvas.drawText(text, x, y, style.getPaint());
    }

}
