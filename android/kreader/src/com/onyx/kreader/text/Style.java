package com.onyx.kreader.text;

import android.graphics.Paint;

/**
 * Created by zengzhu on 1/9/16.
 */
public interface Style {

    public Paint getPaint();

    public float measureWidth(final String text);

    public float measureHeight(final String text);
}
