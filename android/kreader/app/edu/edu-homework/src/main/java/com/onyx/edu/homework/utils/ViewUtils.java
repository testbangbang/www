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

}
