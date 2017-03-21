package com.onyx.android.sdk.utils;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Joy on 2016/5/13.
 */
public class Debug {
    private static final String TAG = Debug.class.getSimpleName();

    private static boolean debug = false;

    public static void setDebug(boolean debug) {
        Debug.debug = debug;
    }

    public static boolean getDebug() {
        return debug;
    }

    public static void i(final String msg) {
        if (debug) {
            Log.i(TAG, msg);
        }
    }

    public static void i(final Class<?> cls, final String msg, final Object... args) {
        if (debug) {
            Log.i(verifyTag(cls.getSimpleName()), formatString(msg, args));
        }
    }

    public static void d(final String msg) {
        if (debug) {
            Log.d(TAG, msg);
        }
    }

    public static void d(final Class<?> cls, final String msg, final Object... args) {
        if (debug) {
            Log.d(verifyTag(cls.getSimpleName()), formatString(msg, args));
        }
    }

    public static void w(final String msg) {
        Log.w(TAG, msg);
    }

    public static void w(final Class<?> cls, final String msg, final Object... args) {
        Log.w(verifyTag(cls.getSimpleName()), formatString(msg, args));
    }

    public static void w(final Throwable throwable) {
        Log.w(TAG, throwable);
    }

    public static void w(final Class<?> cls, final Throwable throwable) {
        Log.w(verifyTag(cls.getSimpleName()), throwable);
    }

    public static void e(final String msg) {
        Log.e(TAG, msg);
    }

    public static void e(final Class<?> cls, final String msg, final Object... args) {
        Log.e(verifyTag(cls.getSimpleName()), formatString(msg, args));
    }

    public static void e(final Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        Log.e(TAG, sw.toString());
    }

    public static void e(final Class<?> cls, final Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        Log.e(verifyTag(cls.getSimpleName()), sw.toString());
    }

    private static String formatString(final String str, final Object... args) {
        return String.format(null, str, args);
    }

    private static String verifyTag(String tag) {
        if (!StringUtils.isBlank(tag)) {
            return tag;
        }
        return TAG;
    }
}
