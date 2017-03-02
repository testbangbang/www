package com.onyx.android.sdk.reader.common;

import android.util.Log;

import com.onyx.android.sdk.reader.BuildConfig;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by Joy on 2016/5/13.
 */
public class Debug {
    private static final String TAG = Debug.class.getSimpleName();
    private static boolean debug = false;

    public static void d(final String msg, final Object... args) {
        d(TAG, msg, args);
    }

    public static void d(final Class<?> cls, final String msg, final Object... args) {
        d(cls.getSimpleName(), msg, args);
    }

    public static void d(final String tag, final String msg, final Object... args) {
        if (debug) {
            Log.d(verifyTag(tag), formatString(msg, args));
        }
    }

    public static void w(final Class<?> cls, final String msg, final Object... args) {
        e(cls.getSimpleName(), msg, args);
    }

    public static void w(final Class<?> cls, final Throwable throwable) {
        Log.w(cls.getSimpleName(), throwable);
    }

    public static void w(final String tag, final Throwable throwable) {
        Log.w(verifyTag(tag), throwable);
    }

    public static void e(final Class<?> cls, final String msg, final Object... args) {
        e(cls.getSimpleName(), msg, args);
    }

    public static void e(final String tag, final String msg, final Object... args) {
        Log.e(verifyTag(tag), formatString(msg, args));
    }

    private static String formatString(final String str, final Object... args) {
        return String.format(null, str, args);
    }

    private static String verifyTag(final String tag) {
        return StringUtils.isBlank(tag) ? TAG : tag;
    }
}
