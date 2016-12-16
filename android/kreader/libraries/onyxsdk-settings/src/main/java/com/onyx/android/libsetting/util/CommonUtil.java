package com.onyx.android.libsetting.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.view.ContextThemeWrapper;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import com.onyx.android.libsetting.R;

/**
 * Created by solskjaer49 on 2016/12/5 11:40.
 */

public class CommonUtil {
    public static boolean apiLevelCheck(int requireAPILevel) {
        return Build.VERSION.SDK_INT >= requireAPILevel;
    }

    // without these,preference which add by code will lost theme custom.
    // ref link:https://medium.com/@arasthel92/dynamically-creating-preferences-on-android-ecc56e4f0789#.q76mon31v
    public static ContextWrapper buildDynamicPreferenceNeededContextWrapper(Context context) {

        // We need to set a TypedValue instance that will be used to retrieve the theme id
        TypedValue themeTypedValue = new TypedValue();

        // We load our 'preferenceTheme' Theme attr into themeTypedValue
        context.getTheme().resolveAttribute(R.attr.preferenceTheme, themeTypedValue, true);

        // We create a ContextWrapper which holds a reference to out Preference Theme
        return new ContextThemeWrapper(context, themeTypedValue.resourceId);
    }

    public static String msToMinuteStringWithUnit(Context context, int ms) {
        if (ms > 0) {
            return context.getResources().getString(R.string.minute, ms / 60000);
        } else {
            return context.getResources().getString(R.string.never);
        }
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * get height of screen without status bar.
     */
    public static int getWindowHeight(Context context) {
        int windowHeight;
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay();
        d.getMetrics(dm);
        Point point = new Point();
        if (apiLevelCheck(Build.VERSION_CODES.JELLY_BEAN_MR1)) {
            d.getRealSize(point);
            windowHeight = point.y - getStatusBarHeight(context);
        } else {
            windowHeight = dm.heightPixels;
        }
        return windowHeight;
    }

}
