package com.onyx.kreader.api;

import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Created by zhuzeng on 2/13/16.
 */
public class ReaderHitTestArgs {

    public String pageName;
    public RectF pageDisplayRect;
    public int pageDisplayOrientation;
    public PointF point;

    public ReaderHitTestArgs(final String name, final RectF rect, int orientation, final PointF p) {
        pageName = name;
        pageDisplayRect = rect;
        point = p;
        pageDisplayOrientation = orientation;
    }

}
