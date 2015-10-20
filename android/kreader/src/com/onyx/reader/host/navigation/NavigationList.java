package com.onyx.reader.host.navigation;

import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 10/16/15.
 */
public class NavigationList {

    // normalized
    private List<RectF> subScreenList = new ArrayList<RectF>();
    private float actualScale = 1.0f;
    private int currentIndex = 0;
    private RectF limitedRect;

    public NavigationList() {
    }

    public void setLimitedRect(final RectF limit) {
        limitedRect = limit;
    }

    public final RectF getLimitedRect() {
        return limitedRect;
    }

    public void setActualScale(final float scale) {
        actualScale = scale;
    }

    public final float getActualScale() {
        return actualScale;
    }

    public void addAll(final List<RectF> list) {
        subScreenList.clear();
        subScreenList.addAll(list);
    }

    public boolean next() {
        if (currentIndex >= subScreenList.size() - 1) {
            return false;
        }
        ++currentIndex;
        return true;
    }

    public boolean prev() {
        if (currentIndex <= 0) {
            return false;
        }
        --currentIndex;
        return true;
    }

    public boolean first() {
        currentIndex = 0;
        return true;
    }

    public boolean last() {
        currentIndex = subScreenList.size() - 1;
        return true;
    }

    public boolean navigateTo(int index) {
        if (index < 0 || index >= subScreenList.size()) {
            return false;
        }
        currentIndex = index;
        return true;
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
