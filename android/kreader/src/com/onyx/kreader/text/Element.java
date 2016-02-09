package com.onyx.kreader.text;

import android.graphics.Canvas;

import java.util.List;

/**
 * Created by zengzhu on 1/9/16.
 * TODO:
 * 1. reference to model
 *
 */
public interface Element {

    public int type();

    public float measureWidth();

    public float measureHeight();

    public float spacing();

    public float baseLine();

    public boolean canLayout(final TextLayoutContext textLayoutContext);

    public boolean canScale();

    public boolean useDedicatedParagraph();

    /**
     * @return if this element can be placed at the beginning of line.
     */
    public boolean canBeLayoutedAtLineBegin();


    public List<Element> breakElement(final float leftWidth);

    public Style style();

    public void setElementX(final float x);

    public void setElementY(final float y);

    public void setElementPosition(final float x, final float y);

    public void draw(final Canvas canvas);

}
