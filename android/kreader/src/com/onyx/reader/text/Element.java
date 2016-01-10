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

    public List<Element> breakElement(final float leftWidth, final float additionalWidth);

    public Style style();

    public void setX(final float x);

    public void setY(final float y);

    public void setPosition(final float x, final float y);

    public void draw(final Canvas canvas);

}
