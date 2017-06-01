package com.onyx.android.sdk.reader.utils;

import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    static public List<RectF> cutRectByExcludingRegions(RectF source, final List<RectF> excluding) {
        List<RectF> result = new ArrayList<>();

        List<RectF> excludeByLeft = new ArrayList<>(excluding);
        Collections.sort(excludeByLeft, new Comparator<RectF>() {
            @Override
            public int compare(RectF o1, RectF o2) {
                return (int)(o1.left - o2.left);
            }
        });
        RectF leftMost = excludeByLeft.get(0);

        RectF bound = new RectF(excluding.get(0));
        for (RectF r : excluding) {
            bound.union(r);
        }

        // out bounding rectangles of regions to exclude
        if (bound.left > source.left) {
            result.add(new RectF(source.left, source.top, bound.left, source.bottom));
        }
        if (bound.right < source.right) {
            result.add(new RectF(bound.right, source.top, source.right, source.bottom));
        }
        if (bound.top > source.top) {
            result.add(new RectF(bound.left, source.top, bound.right, bound.top));
        }
        if (bound.bottom < source.bottom) {
            result.add(new RectF(bound.left, bound.bottom, bound.right, source.bottom));
        }

        if (excluding.size() <= 1) {
            return result;
        }

        RectF nextLeft = null;
        List<RectF> leftList = new ArrayList<>();
        for (RectF rect : excludeByLeft) {
            if (rect.left == leftMost.left) {
                leftList.add(rect);
            } else {
                // nextLeft can be null, in this case,
                // we think all excluding rectangles align on the left
                nextLeft = rect;
                break;
            }
        }

        Collections.sort(leftList, new Comparator<RectF>() {
            @Override
            public int compare(RectF o1, RectF o2) {
                return (int)(o1.right - o2.right);
            }
        });

        float right = leftList.get(0).right;
        if (nextLeft != null) {
            right = Math.min(right, nextLeft.left);
        }

        List<RectF> topList = new ArrayList<>(leftList);
        Collections.sort(topList, new Comparator<RectF>() {
            @Override
            public int compare(RectF o1, RectF o2) {
                return (int)(o1.top - o2.top);
            }
        });

        RectF top = topList.get(0);
        if (top.top > bound.top) {
            result.add(new RectF(bound.left, bound.top, right, top.top));
        }

        RectF bottom = topList.get(topList.size() - 1);
        if (bottom.bottom < bound.bottom) {
            result.add(new RectF(bound.left, bottom.bottom, right, bound.bottom));
        }

        for (int j = 1; j < topList.size(); j++) {
            RectF above = topList.get(j - 1);
            RectF below = topList.get(j);
            if (below.top > above.bottom) {
                result.add(new RectF(bound.left, above.bottom, right, below.top));
            }
        }

        List<RectF> removeList = new ArrayList<>();
        for (RectF rect : leftList) {
            if (Float.compare(rect.right, right) <= 0) {
                removeList.add(rect);
            } else {
                rect.left = right;
            }
        }

        for (RectF rect : removeList) {
            excludeByLeft.remove(rect);
        }

        bound.left = right;
        result.addAll(cutRectByExcludingRegions(bound, excludeByLeft));

        return result;
    }

}
