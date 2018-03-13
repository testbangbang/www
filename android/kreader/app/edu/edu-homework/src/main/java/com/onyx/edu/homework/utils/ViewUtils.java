package com.onyx.edu.homework.utils;

import android.graphics.Rect;
import android.view.View;

/**
 * Created by lxm on 2017/12/6.
 */

public class ViewUtils {

    public static boolean isVisibleLocal(View target){
        Rect rect =new Rect();
        target.getLocalVisibleRect(rect);
        return rect.top == 0;
    }

    /**
     * Set a view visibility to VISIBLE (true) or GONE (false).
     *
     * @param visible True for VISIBLE, false for GONE.
     */
    public static void setGone(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * Set a view visibility to VISIBLE (true) or INVISIBLE (false).
     *
     * @param visible True for VISIBLE, false for INVISIBLE.
     */
    public static void setVisible(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

}
