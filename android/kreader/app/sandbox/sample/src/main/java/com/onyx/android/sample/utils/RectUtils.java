package com.onyx.android.sample.utils;

import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
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
            List<Rect> temp = new ArrayList<>();
            createList(parent, child, temp);

            // clean up if not in parent and child
            List<Rect> list = new ArrayList<>();
            for(Rect rect : temp) {
                if (parent.contains(rect) || child.contains(rect)) {
                    list.add(rect);
                }
            }

            // clean up if item contains the other
            temp.clear();
            for(int i = 0; i < list.size() - 1; ++i) {
                boolean found = false;
                for(int j = i + 1; j < list.size(); ++j) {
                    if (list.get(j).contains(list.get(i))) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    temp.add(list.get(i));
                }
            }
            return temp;
        }
    }

    public static void createList(final Rect parent, final int x, final int y, final List<Rect> list) {
        list.add(createRect(x, y, parent.left, parent.top));
        list.add(createRect(x, y, parent.right, parent.top));
        list.add(createRect(x, y, parent.left, parent.bottom));
        list.add(createRect(x, y, parent.right, parent.bottom));
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
