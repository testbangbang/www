package com.onyx.reader.plugin;

import android.graphics.PointF;

/**
 * Created by zhuzeng on 10/3/15.
 */
public interface ReaderHitTestManager {

    /**
     * Retrieve document coordinates from view coordinates.
     * @param viewPoint The view coordinates.
     * @param documentPoint The returned document coordinates.
     * @return false if failed.
     */
    public boolean viewToDoc(final PointF viewPoint, final PointF documentPoint);


    public ReaderTextSelection selectWord(final PointF viewPoint);


    public ReaderTextSelection select(final PointF startPoint, final PointF endPoint);


}
