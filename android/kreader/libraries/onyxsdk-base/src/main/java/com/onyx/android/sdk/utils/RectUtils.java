package com.onyx.android.sdk.utils;

import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.MutableFloat;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by zhuzeng on 2/11/16.
 */
public class RectUtils {

    static public Rect toRect(final RectF source) {
        return new Rect((int)source.left, (int)source.top, (int)source.right, (int)source.bottom);
    }

    static public List<Rect> toRectList(final List<RectF> source) {
        ArrayList<Rect> list = new ArrayList<>();
        for (RectF r : source) {
            list.add(toRect(r));
        }
        return list;
    }

    static public RectF toRectF(final Rect source) {
        return new RectF(source.left, source.top, source.right, source.bottom);
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

        if (excluding == null || excluding.size() <= 0) {
            result.add(source);
            return result;
        }

        List<RectF> excludeByLeft = new ArrayList<>(excluding);
        Collections.sort(excludeByLeft, new Comparator<RectF>() {
            @Override
            public int compare(RectF o1, RectF o2) {
                return Float.compare(o1.left, o2.left);
            }
        });

        RectF outBound = new RectF(source);

        do {
            RectF leftMost = excludeByLeft.get(0);

            RectF innerBound = new RectF(excludeByLeft.get(0));
            for (RectF r : excludeByLeft) {
                innerBound.union(r);
            }

            // out bounding rectangles of regions to exclude
            if (innerBound.left > outBound.left) {
                result.add(new RectF(outBound.left, outBound.top, innerBound.left, outBound.bottom));
            }
            if (innerBound.right < outBound.right) {
                result.add(new RectF(innerBound.right, outBound.top, outBound.right, outBound.bottom));
            }
            if (innerBound.top > outBound.top) {
                result.add(new RectF(innerBound.left, outBound.top, innerBound.right, innerBound.top));
            }
            if (innerBound.bottom < outBound.bottom) {
                result.add(new RectF(innerBound.left, innerBound.bottom, innerBound.right, outBound.bottom));
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
                    return Float.compare(o1.right, o2.right);
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
                    return Float.compare(o1.top, o2.top);
                }
            });

            List<Pair<AtomicReference<Float>, AtomicReference<Float>>> segmentsByY = new ArrayList<>();
            for (RectF r : topList) {
                if (segmentsByY.isEmpty()) {
                    segmentsByY.add(new Pair<>(new AtomicReference<>(r.top), new AtomicReference<>(r.bottom)));
                    continue;
                }
                boolean combined = false;
                for (Pair<AtomicReference<Float>, AtomicReference<Float>> segment : segmentsByY) {
                    if (r.bottom < segment.first.get() || r.top > segment.second.get()) {
                        continue;
                    }

                    if (r.top < segment.first.get()) {
                        segment.first.set(r.top);
                    }
                    if (r.bottom > segment.second.get()) {
                        segment.second.set(r.bottom);
                    }
                    combined = true;
                    break;
                }
                if (!combined) {
                    segmentsByY.add(new Pair<>(new AtomicReference<>(r.top), new AtomicReference<>(r.bottom)));
                }
            }

            Collections.sort(segmentsByY, new Comparator<Pair<AtomicReference<Float>, AtomicReference<Float>>>() {
                @Override
                public int compare(Pair<AtomicReference<Float>, AtomicReference<Float>> o1, Pair<AtomicReference<Float>, AtomicReference<Float>> o2) {
                    return Float.compare(o1.first.get(), o2.first.get());
                }
            });

            Pair<AtomicReference<Float>, AtomicReference<Float>> top = segmentsByY.get(0);
            if (top.first.get() > innerBound.top) {
                result.add(new RectF(innerBound.left, innerBound.top, right, top.first.get()));
            }

            for (int j = 1; j < segmentsByY.size(); j++) {
                Pair<AtomicReference<Float>, AtomicReference<Float>> above = segmentsByY.get(j - 1);
                Pair<AtomicReference<Float>, AtomicReference<Float>> below = segmentsByY.get(j);
                if (below.first.get() > above.second.get()) {
                    result.add(new RectF(innerBound.left, above.second.get(), right, below.first.get()));
                }
            }

            Collections.sort(segmentsByY, new Comparator<Pair<AtomicReference<Float>, AtomicReference<Float>>>() {
                @Override
                public int compare(Pair<AtomicReference<Float>, AtomicReference<Float>> o1, Pair<AtomicReference<Float>, AtomicReference<Float>> o2) {
                    return Float.compare(o1.second.get(), o2.second.get());
                }
            });

            Pair<AtomicReference<Float>, AtomicReference<Float>> bottom = segmentsByY.get(segmentsByY.size() - 1);
            if (bottom.second.get() < innerBound.bottom) {
                result.add(new RectF(innerBound.left, bottom.second.get(), right, innerBound.bottom));
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

            outBound = new RectF(innerBound);
            outBound.left = right;
        } while (excludeByLeft.size() > 0);

        return result;
    }

    static private void addToUniqueList(final List<RectF> list, RectF rect) {
        boolean find = false;
        for (RectF r : list) {
            if (r.left == rect.left && r.top == rect.top &&
                    r.right == rect.right && r.bottom == rect.bottom) {
                find = true;
                break;
            }
        }
        if (!find) {
            list.add(rect);
        }
    }

    static public float square(final List<RectF> list) {
        float square = 0;
        if (list == null || list.size() <= 0) {
            return 0;
        }

        for (RectF r : list) {
            Debug.e(RectUtils.class, "square rect -> " + r.toString());
        }

        if (list.size() == 1) {
            square = list.get(0).width() * list.get(0).height();
            Debug.e(RectUtils.class, "square result -> " + square);
            return square;
        }

        Map<Integer, List<RectF>> intersectionMap = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                RectF r1 = new RectF(list.get(i));
                RectF r2 = new RectF(list.get(j));
                if (r1.intersect(r2)) {
                    Debug.e(RectUtils.class, "intersects: %d, %d -> %s", i, j, r1.toString());
                    if (intersectionMap.get(i) == null) {
                        intersectionMap.put(i, new ArrayList<RectF>());
                    }
                    addToUniqueList(intersectionMap.get(i), r1);
                    if (intersectionMap.get(j) == null) {
                        intersectionMap.put(j, new ArrayList<RectF>());
                    }
                    addToUniqueList(intersectionMap.get(j), r1);
                }
            }
            RectF r = list.get(i);
            float s1 = r.width() * r.height();
            float s2 = square(intersectionMap.get(i));
            square += (s1 - s2);
            Debug.e(RectUtils.class, "%s -> %f, intersections -> %f, square -> %f", r.toString(), s1, s2, square);
        }

        List<RectF> intersections = new ArrayList<>();
        for (List<RectF> l : intersectionMap.values()) {
            for (RectF r : l) {
                addToUniqueList(intersections, r);
            }
        }
        float s1 = square(intersections);
        Debug.e(RectUtils.class, "intersections square -> " + s1);
        square += s1;
        Debug.e(RectUtils.class, "square result -> " + square);
        return square;
    }

}
