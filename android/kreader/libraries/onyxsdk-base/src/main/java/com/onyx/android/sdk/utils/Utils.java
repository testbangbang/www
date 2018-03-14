package com.onyx.android.sdk.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * <pre>
 *     author : liao lin tao
 *     time   : 2018/3/14 18:20
 *     desc   :
 * </pre>
 */

public class Utils {
    @SuppressLint("StaticFieldLeak")
    private static Application sApplication;

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void init(@NonNull final Application app) {
        Utils.sApplication = app;
    }

    @Nullable
    public static Application getApp() {
        return sApplication;
    }
}
