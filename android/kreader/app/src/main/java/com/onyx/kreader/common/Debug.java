package com.onyx.kreader.common;

import android.util.Log;
import com.onyx.kreader.BuildConfig;

/**
 * Created by Joy on 2016/5/13.
 */
public class Debug {
    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }
}
