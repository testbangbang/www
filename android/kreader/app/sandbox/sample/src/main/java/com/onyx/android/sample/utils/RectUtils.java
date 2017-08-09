package com.onyx.android.sample.utils;

import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuzeng on 08/08/2017.
 */

public class RectUtils {

    public static class RectResult {
        public Rect parent;
        public Rect child;
        public Rect[] inParent = new Rect[4];
        public Rect[] inChild = new Rect[4];

        public void reset() {
            inParent = new Rect[4];
            inChild = new Rect[4];
        }

        public List<Rect>  generate() {
            List<Integer> x = new ArrayList<>();
            List<Integer> y = new ArrayList<>();
            x.add(parent.left);
            x.add(parent.right);
            x.add(child.left);
            x.add(child.right);
            Collections.sort(x);


            y.add(parent.top);
            y.add(parent.bottom);
            y.add(child.top);
            y.add(child.bottom);
            Collections.sort(y);


            List<Rect> temp = new ArrayList<>();
            for(int i = 0; i < x.size() - 1; ++i) {
                for(int j = 0; j < y.size() - 1; ++j) {
                    add(temp, createRect(x.get(i), y.get(j), x.get(i + 1), y.get(j + 1)));
                }
            }
            temp = cleanup(temp);
            temp = merge(temp);
            return temp;
        }

        public List<Rect> cleanup(final List<Rect> list) {
            List<Rect> temp = new ArrayList<>();
            for(Rect rect : list) {
                if (rect.contains(parent) || rect.contains(child)){
                    continue;
                }
                if (parent.contains(rect) || child.contains(rect)) {
                    temp.add(rect);
                }
            }
            return temp;
        }

        public List<Rect> merge(final List<Rect> list) {
            List<Rect> temp = new ArrayList<>();
            for(int i = 0; i < list.size(); ++i) {
                for(int j = 0; j < list.size(); ++j) {
                    if (canMerge(list.get(j), list.get(i))) {
                        list.get(i).union(list.get(j));
                        list.set(j, null);
                    }
                }
            }
            temp.clear();
            for(Rect rect : list) {
                if (rect != null) {
                    temp.add(rect);
                }
            }
            return temp;
        }

        public List<Rect> generate2() {
            List<Rect> temp = new ArrayList<>();
            createList(parent, child, temp);

            // clean up if not in parent and child, remove invalid rects.
            List<Rect> list = new ArrayList<>();
            for(Rect rect : temp) {
                if (rect.contains(parent) || rect.contains(child)){
                    continue;
                }
                if (parent.contains(rect) || child.contains(rect)) {
                    list.add(rect);
                }
            }

            // merge hortionzally
            for(int i = 0; i < list.size(); ++i) {
                for(int j = 0; j < list.size(); ++j) {
                    if (canMerge(list.get(j), list.get(i))) {
                        list.get(i).union(list.get(j));
                        list.set(j, null);
                    }
                }
            }
            temp.clear();
            for(Rect rect : list) {
                if (rect != null) {
                    temp.add(rect);
                }
            }
            list.clear();
            list.addAll(temp);


            // clean up if item contains the other
            temp.clear();
            for(int i = 0; i < list.size(); ++i) {
                boolean found = false;
                for(int j = 0; j < list.size(); ++j) {
                    final Rect first = list.get(i);
                    final Rect second = list.get(j);
                    if (!first.equals(second)) {
                        if (first.contains(second)) {
                            found = true;
                            add(temp, first);
                        } else if (second.contains(first)) {
                            found = true;
                            add(temp, second);
                        }
                    }
                }
                if (!found) {
                    temp.add(list.get(i));
                }
            }

            // clean up intersets
            list.clear();
            list.addAll(temp);
            temp.clear();
            for(int i = 0; i < list.size(); ++i) {
                boolean found = false;
                for(int j = 0; j < list.size(); ++j) {
                    final Rect first = list.get(i);
                    final Rect second = list.get(j);
                    if (!first.equals(second)) {
                        if (first.contains(second)) {
                            found = true;
                            add(temp, first);
                        } else if (second.contains(first)) {
                            found = true;
                            add(temp, second);
                        }
                    }
                }
                if (!found) {
                    temp.add(list.get(i));
                }
            }

            return temp;
        }
    }

    public static void add(final List<Rect> list, final Rect rect) {
        if (list.indexOf(rect) >= 0) {
            return;
        }
        list.add(rect);
    }

    public static void checkAndAdd(final List<Rect> list, final Rect rect) {
        for(int i = 0; i < list.size(); ++i) {
            final Rect r = list.get(i);
            if (r.contains(rect)) {
                return;
            }
        }
        list.add(rect);
    }

    public static boolean canMerge(final Rect first, final Rect second)  {
        if (first == null || second == null) {
            return false;
        }
        if (first.equals(second)) {
            return false;
        }
        if (first.contains(second) || second.contains(first)) {
            return true;
        }
        if (first.top == second.top && first.height() == second.height()) {
            if ((second.left >= first.left && second.left <= first.right) ||
                (first.left >= second.left && first.left <= second.right)) {
                return true;
            }
        }
        return false;
    }

    public static Rect union(final Rect first, final Rect second) {
        Rect rect = new Rect(first);
        rect.union(second);
        return rect;
    }


    public static void createList(final Rect parent, final int x, final int y, final List<Rect> list) {
        checkAndAdd(list, createRect(x, y, parent.left, parent.top));
        checkAndAdd(list, createRect(x, y, parent.right, parent.top));
        checkAndAdd(list, createRect(x, y, parent.left, parent.bottom));
        checkAndAdd(list, createRect(x, y, parent.right, parent.bottom));
    }

    public static void createList(final Rect parent, final Rect child, final List<Rect> list) {
        createList(parent, child.left, child.top, list);
        createList(parent, child.right, child.top, list);
        createList(parent, child.left, child.bottom, list);
        createList(parent, child.right, child.bottom, list);
    }

    public static Rect createRect(int x1, int y1, int x2, int y2) {
        int left = Math.min(x1, x2);
        int top = Math.min(y1, y2);
        int right = Math.max(x1, x2);
        int bottom = Math.max(y1, y2);
        return new Rect(left, top, right, bottom);
    }

}
