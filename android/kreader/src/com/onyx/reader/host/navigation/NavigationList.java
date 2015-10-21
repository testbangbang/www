package com.onyx.reader.host.navigation;

import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 10/16/15.
 * Make sure the rectangles are normalized in [0, 1]
 */
public class NavigationList {

    private List<RectF> subScreenList = new ArrayList<RectF>();
    private int currentIndex = -1;
    private RectF limitedRect;

    static public NavigationList rowsLeftToRight(int rows, int cols, final RectF limited) {
        RectF child = new RectF(0, 0, 1, 1);
        List<RectF> list = new ArrayList<RectF>();
        for(int r = 0; r < rows; ++r) {
            for(int c = 0; c < cols; ++c) {
                float left = child.left + child.width() / cols * c;
                float right = left + child.width() / cols;
                float top = child.top + child.height() / rows * r;
                float bottom = top + child.height() / rows;
                RectF sub = new RectF(left, top, right, bottom);
                list.add(sub);
            }
        }
        NavigationList navigationList = new NavigationList();
        navigationList.setLimitedRect(limited);
        navigationList.addAll(list);
        return navigationList;
    }

    static public NavigationList rowsRightToLeft(int rows, int cols, final RectF limited) {
        RectF child = new RectF(0, 0, 1, 1);
        List<RectF> list = new ArrayList<RectF>();
        for(int r = 0; r < rows; ++r) {
            for(int c = 0; c < cols; ++c) {
                float left = child.left + child.width() / cols * (cols - c - 1);
                float right = left + child.width() / cols;
                float top = child.top + child.height() / rows * r;
                float bottom = top + child.height() / rows;
                RectF sub = new RectF(left, top, right, bottom);
                list.add(sub);
            }
        }
        NavigationList navigationList = new NavigationList();
        navigationList.setLimitedRect(limited);
        navigationList.addAll(list);
        return navigationList;
    }

    static public NavigationList columnsLeftToRight(int rows, int cols, final RectF limited) {
        RectF child = new RectF(0, 0, 1, 1);
        List<RectF> list = new ArrayList<RectF>();
        for(int c = 0; c < cols; ++c) {
            for(int r = 0; r < rows; ++r) {
                float left = child.left + child.width() / cols * c;
                float right = left + child.width() / cols;
                float top = child.top + child.height() / rows * r;
                float bottom = top + child.height() / rows;
                RectF sub = new RectF(left, top, right, bottom);
                list.add(sub);
            }
        }
        NavigationList navigationList = new NavigationList();
        navigationList.setLimitedRect(limited);
        navigationList.addAll(list);
        return navigationList;
    }

    static public NavigationList columnsRightToLeft(int rows, int cols, final RectF limited) {
        RectF child = new RectF(0, 0, 1, 1);
        List<RectF> list = new ArrayList<RectF>();
        for(int c = 0; c < cols; ++c) {
            for(int r = 0; r < rows; ++r) {
                float left = child.left + child.width() / cols * (cols - c - 1);
                float right = left + child.width() / cols;
                float top = child.top + child.height() / rows * r;
                float bottom = top + child.height() / rows;
                RectF sub = new RectF(left, top, right, bottom);
                list.add(sub);
            }
        }
        NavigationList navigationList = new NavigationList();
        navigationList.setLimitedRect(limited);
        navigationList.addAll(list);
        return navigationList;
    }

    public NavigationList() {
    }

    public void setLimitedRect(final RectF limit) {
        limitedRect = limit;
    }

    public final RectF getLimitedRect() {
        return limitedRect;
    }

    public void addAll(final List<RectF> list) {
        subScreenList.clear();
        subScreenList.addAll(list);
    }

    public boolean hasNext() {
        if (currentIndex >= subScreenList.size() - 1) {
            return false;
        }
        return true;
    }

    public RectF next() {
        if (hasNext()) {
            ++currentIndex;
            return getCurrent();
        }
        return null;
    }

    public boolean hasPrevious() {
        if (currentIndex <= 0) {
            return false;
        }
        return true;
    }

    public RectF previous() {
        if (hasPrevious()) {
            --currentIndex;
            return getCurrent();
        }
        return null;
    }

    public RectF first() {
        currentIndex = 0;
        return getCurrent();
    }

    public RectF last() {
        currentIndex = subScreenList.size() - 1;
        return getCurrent();
    }

    public RectF navigateTo(int index) {
        if (index < 0 || index >= subScreenList.size()) {
            return null;
        }
        currentIndex = index;
        return getCurrent();
    }

    public RectF getCurrent() {
        if (currentIndex >= 0 && currentIndex < subScreenList.size()) {
            RectF current = new RectF(subScreenList.get(currentIndex));
            if (limitedRect != null) {
                current.intersect(limitedRect);
            }
            return current;
        }
        return null;
    }



}
