package com.onyx.android.note.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by solskjaer49 on 16/6/24 11:54.
 */

public class Util {
    public static int screenWidth, screenHeight;
    static final String TAG = Util.class.getSimpleName();

    public static SimpleDateFormat getDateFormat(Locale locale) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale);
    }

    public static void updateVisualInfo(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) {
            Log.w(TAG, "WINDOW_SERVICE is not ready");
            return;
        }
        wm.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }
}
