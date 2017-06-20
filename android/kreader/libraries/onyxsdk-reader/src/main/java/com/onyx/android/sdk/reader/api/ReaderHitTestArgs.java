package com.onyx.android.sdk.reader.api;

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

    /**
     *
     * @param name the page name
     * @param rect the page display rect.
     * @param orientation the page display orientation.
     * @param p the touch point relative to display rect.
     */
    public ReaderHitTestArgs(final String name, final RectF rect, int orientation, final PointF p) {
        pageName = name;
        pageDisplayRect = rect;
        point = p;
        pageDisplayOrientation = orientation;
    }

}
