package com.onyx.kreader.common;

import android.util.Log;
import com.onyx.kreader.BuildConfig;

/**
 * Created by Joy on 2016/5/13.
 */
public class Debug {
    private static final String TAG = Debug.class.getSimpleName();
    private static boolean debug = true;

    public static void d(final String msg, final Object... args) {
        d(TAG, msg, args);
    }

    public static void d(final String tag, final String msg, final Object... args) {
        if (BuildConfig.DEBUG && debug) {
            Log.d(tag, formatString(msg, args));
        }
    }

    public static void e(final String msg, final Object... args) {
        e(TAG, msg, args);
    }

    public static void e(final String tag, final String msg, Object... args) {
        Log.e(tag, formatString(msg, args));
    }

    private static String formatString(String str, Object... args) {
        return String.format(null, str, args);
    }
}
