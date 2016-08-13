package com.onyx.kreader.common;

import android.util.Log;
import com.onyx.kreader.BuildConfig;

/**
 * Created by Joy on 2016/5/13.
 */
public class Debug {
    private static final String TAG = Debug.class.getSimpleName();
    private static boolean debug = true;

    public static void d(String msg) {
        d(TAG, msg);
    }

    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG && debug) {
            Log.d(tag, msg);
        }
    }
}
