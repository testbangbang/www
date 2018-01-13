package com.onyx.jdread.reader.common;

import android.graphics.PointF;

/**
 * Created by huxiaomao on 2018/1/11.
 */

public class SelectWordInfo {
    public String pagePosition;
    public PointF startPoint;
    public PointF endPoint;
    public PointF touchPoint;

    public SelectWordInfo(String pagePosition, PointF startPoint, PointF endPoint, PointF touchPoint) {
        this.pagePosition = pagePosition;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.touchPoint = touchPoint;
    }
}
