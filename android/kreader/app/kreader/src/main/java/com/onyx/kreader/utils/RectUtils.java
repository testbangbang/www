package com.onyx.kreader.utils;

import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import com.alibaba.fastjson.JSON;
import com.onyx.kreader.common.Debug;

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

    static public PointF getTopLeft(List<RectF> list) {
        RectF target = list.get(0);
        for (RectF rect : list) {
            int compare = compareBaseLine(rect, target);
            if (compare == 0) {
                if (rect.left < target.left) {
                    target = rect;
                }
            } else if (compare < 0) {
                target = rect;
            }
        }
        Debug.d("getTopLeft, target: " + JSON.toJSONString(target) + ", " + JSON.toJSONString(list));
        return new PointF(target.left, target.top);
    }

    static public PointF getBottomLeft(List<RectF> list) {
        RectF target = list.get(0);
        for (RectF rect : list) {
            int compare = compareBaseLine(rect, target);
            if (compare == 0) {
                if (rect.left < target.left) {
                    target = rect;
                }
            } else if (compare < 0) {
                target = rect;
            }
        }
        Debug.d("getBottomLeft, target: " + JSON.toJSONString(target) + ", " + JSON.toJSONString(list));
        return new PointF(target.left, target.bottom);
    }

    static public PointF getBottomRight(List<RectF> list) {
        RectF target = list.get(0);
        for (RectF rect : list) {
            int compare = compareBaseLine(rect, target);
            if (compare == 0) {
                if (rect.right > target.right) {
                    target = rect;
                }
            } else if (compare > 0) {
                target = rect;
            }
        }
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
        if ((rect1.top < rect2.top && rect1.bottom > rect2.bottom) ||
                (rect2.top < rect1.top && rect2.bottom > rect1.bottom)) {
            // if one rectangle is in the vertical range of another, we treat they as if they are on the same baseline
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
                baseList.add(rect);
            }
        }
        return baseList;
    }

}
