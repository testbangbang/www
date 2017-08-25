package com.onyx.android.dr.util;

import android.content.Context;
import android.os.Build;

import com.onyx.android.dr.R;


/**
 * Created by solskjaer49 on 2016/12/5 11:40.
 */

public class CommonUtil {
    public static boolean apiLevelCheck(int requireAPILevel) {
        return Build.VERSION.SDK_INT >= requireAPILevel;
    }

    public static String msToMinuteStringWithUnit(Context context, int ms) {
        if (ms > 0) {
            return context.getResources().getString(R.string.minute, ms / 60000);
        } else {
            return context.getResources().getString(R.string.never);
        }
    }
}
