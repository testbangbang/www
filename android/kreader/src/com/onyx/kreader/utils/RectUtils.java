package com.onyx.kreader.utils;

import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by zhuzeng on 2/11/16.
 */
public class RectUtils {

    static public Rect toRect(final RectF source) {
        return new Rect((int)source.left, (int)source.top, (int)source.width(), (int)source.height());
    }
}
