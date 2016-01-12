package com.onyx.reader.text;

import android.graphics.Canvas;

import java.util.List;

/**
 * Created by zengzhu on 1/9/16.
 */
public interface Element {

    public int type();

    public float measureWidth();

    public float spacing();

    public float measureHeight();

    public float baseLine();

    public boolean layout(final TextLayoutContext textLayoutContext);

    public boolean canScale();

    /**
     * @return if this element can be placed at the beginning of line.
     */
    public boolean canBeLayoutedAtLineBegin();


    public List<Element> breakElement(final float leftWidth, final float additionalWidth);

    public Style style();

    public void setElementX(final float x);

    public void setElementY(final float y);

    public void setElementPosition(final float x, final float y);

    public void draw(final Canvas canvas);

}
