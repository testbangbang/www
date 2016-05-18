package com.onyx.android.sdk.ui.util;

import android.content.Context;

/**
 * Created by joy on 5/22/14.
 */
public class DensityUtil {
    public static int dip2px(Context context, double dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, double pxValue) {
        final double scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
