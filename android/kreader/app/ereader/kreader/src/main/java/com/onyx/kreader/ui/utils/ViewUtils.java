package com.onyx.kreader.ui.utils;

import android.graphics.Rect;
import android.view.View;

/**
 * Created by joy on 2/1/18.
 */

public class ViewUtils {
    public static Rect getRelativeRect(final View parentView, final View childView) {
        int [] parent = new int[2];
        int [] child = new int[2];
        parentView.getLocationOnScreen(parent);
        childView.getLocationOnScreen(child);
        Rect rect = new Rect();
        childView.getLocalVisibleRect(rect);
        rect.offset(child[0] - parent[0], child[1] - parent[1]);
        return rect;
    }
}
