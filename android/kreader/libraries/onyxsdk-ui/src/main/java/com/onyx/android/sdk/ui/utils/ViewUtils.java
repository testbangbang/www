package com.onyx.android.sdk.ui.utils;

import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by lxm on 2017/9/22.
 */

public class ViewUtils {

    public static void removeGlobalLayoutListener(View view, ViewTreeObserver.OnGlobalLayoutListener victim) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            view.getViewTreeObserver().removeGlobalOnLayoutListener(victim);
        } else {
            view.getViewTreeObserver().removeOnGlobalLayoutListener(victim);
        }
    }
}
