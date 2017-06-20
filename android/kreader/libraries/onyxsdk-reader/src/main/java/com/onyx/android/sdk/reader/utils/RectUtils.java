package com.onyx.android.sdk.reader.utils;

import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 2/11/16.
 */
public class RectUtils {

    static public Rect toRect(final RectF source) {
        return new Rect((int)source.left, (int)source.top, (int)source.width(), (int)source.height());
    }

    static public RectF remove(final RectF parent, final float childTop, final float childHeight, final float spacing) {
        if (childTop + childHeight +  spacing >= parent.bottom) {
            return null;
        }
        return new RectF(parent.left, childTop + childHeight + spacing, parent.right, parent.bottom);
    }

    static public RectF rectangle(double result[]) {
        float left = (float)result[0];
        float top = (float)result[1];
        float right = (float)result[2];
        float bottom = (float)result[3];
        RectF rect = new RectF(left, top , right, bottom);
        return rect;
    }

    static public PointF getBeginTop(List<RectF> list) {
        RectF target = list.get(0);
        return new PointF(target.left, target.top);
    }

    static public PointF getBeginBottom(List<RectF> list) {
        RectF target = list.get(0);
        return new PointF(target.left, target.bottom);
    }

    static public PointF getEndBottom(List<RectF> list) {
        RectF target = list.get(list.size() - 1);
        return new PointF(target.right, target.bottom);
    }

    /**
     * compare base line of two rectangle
     *
     * return 0 if on the same baseline (with tolerance)
     * return positive value if rect1 below rect2
     * return negative value if rect1 above rect2
     *
     * @param rect1
     * @param rect2
     * @return
     */
    static public int compareBaseLine(RectF rect1, RectF rect2) {
        final int tolerance = 10;
        int compare = (int)(rect1.bottom - rect2.bottom);
        if (Math.abs(compare) < tolerance) {
            return 0;
        }
        if ((rect1.top <= rect2.top && rect1.bottom >= rect2.bottom) ||
                (rect2.top <= rect1.top && rect2.bottom >= rect1.bottom)) {
            // if one rectangle is in the vertical range of another, we treat they as if they are on the same baseline
            return 0;
        }
        float overlay = Math.min(rect1.bottom, rect2.bottom) - Math.max(rect1.top, rect2.top);
        if (overlay > rect1.height() / 2 || overlay > rect2.height() / 2) {
            // if overlay is big enough, we think they are on the same baseline
            return 0;
        }
        return compare;
    }

    static public List<RectF> mergeRectanglesByBaseLine(List<RectF> list) {
        List<RectF> baseList = new ArrayList<>();
        for (RectF rect : list) {
            // force-brute iterating two list, since we will not have to much to compare
            boolean foundBaseLine = false;
            for (int i = 0; i < baseList.size(); i++) {
                RectF baseRect = baseList.get(i);
                if (compareBaseLine(rect, baseRect) == 0) {
                    foundBaseLine = true;
                    baseRect.union(rect);
                    break;
                }
            }
            if (!foundBaseLine) {
                baseList.add(new RectF(rect));
            }
        }
        return baseList;
    }

}
