package com.onyx.kreader.text;

import android.graphics.Canvas;

import java.util.List;

/**
 * Created by zengzhu on 1/21/16.
 */
public class LeadingElement implements Element {

    private float x;
    private float y;
    private float width;
    private float height;
    private Style style;

    public static final String SPACE = " ";

    public static LeadingElement create(final Style styleRef) {
        LeadingElement leadingElement = new LeadingElement();
        leadingElement.style = styleRef;
        return leadingElement;
    }

    public LeadingElement() {
    }

    public int type() {
        return 0;
    }

    public float measureWidth() {
        if (width <= 0) {
            width = style.measureWidth(SPACE) * 8;
        }
        return width;
    }

    public float spacing() {
        return 0;
    }

    public float measureHeight() {
        if (height <= 0) {
            height = style.measureHeight(SPACE);
        }
        return height;
    }

    public boolean canLayout(final TextLayoutContext textLayoutContext) {
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

    public boolean useDedicatedParagraph() {
        return false;
    }

    public boolean canBeLayoutedAtLineBegin() {
        return true;
    }

    public List<Element> breakElement(final float leftWidth) {
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
        canvas.drawText(SPACE, x, y, style.getPaint());
    }

}
