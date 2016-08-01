package com.onyx.android.note.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceActivity;
import android.provider.Settings;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.onyx.android.note.BuildConfig;
import com.onyx.android.sdk.data.GObject;
import com.onyx.android.sdk.utils.RawResourceUtil;

/**
 * Created with IntelliJ IDEA. User: zhuzeng Date: 5/18/14 Time: 5:21 PM To change this template use
 * File | Settings | File Templates.
 */
public class NoteAppConfig {

    static private String TAG = NoteAppConfig.class.getSimpleName();
    private GObject backend;
    private static NoteAppConfig globalInstance;

    public static final String HOME_ACTIVITY_PKG_NAME = "home_activity_pkg";
    public static final String HOME_ACTIVITY_CLS_NAME = "home_activity_cls";
    public static final String USE_FULL_SCREEN = "full_screen";

    static public final boolean useDebugConfig = false;

    static public NoteAppConfig sharedInstance(Context context) {
        if (globalInstance == null) {
            globalInstance = new NoteAppConfig(context);
        }
        return globalInstance;
    }

    public final String getHomeActivityPackageName() {
        if (backend.hasKey(HOME_ACTIVITY_PKG_NAME)) {
            return backend.getString(HOME_ACTIVITY_PKG_NAME);
        }
        return null;
    }

    public final String getHomeActivityClassName() {
        if (backend.hasKey(HOME_ACTIVITY_CLS_NAME)) {
            return backend.getString(HOME_ACTIVITY_CLS_NAME);
        }
        return null;
    }

    public boolean useFullScreen() {
        return backend.hasKey(USE_FULL_SCREEN) && backend.getBoolean(USE_FULL_SCREEN);
    }

    private NoteAppConfig(Context context) {
        backend = objectFromDebugModel(context);
        if (backend != null) {
            Log.i(TAG, "Using debug model.");
            return;
        }

        backend = objectFromManufactureAndModel(context);
        if (backend != null) {
            Log.i(TAG, "Using manufacture model.");
            return;
        }

        backend = objectFromModel(context);
        if (backend != null) {
            Log.i(TAG, "Using device model.");
            return;
        }

        Log.i(TAG, "Using default model.");
        backend = objectFromDefaultOnyxConfig(context);
    }

    private GObject objectFromManufactureAndModel(Context context) {
        final String name = Build.MANUFACTURER + "_" + Build.MODEL;
        return objectFromRawResource(context, name);
    }

    private GObject objectFromDebugModel(Context context) {
        if (BuildConfig.DEBUG && useDebugConfig) {
            return objectFromRawResource(context, "debug");
        }
        return null;
    }

    private GObject objectFromModel(Context context) {
        final String name = Build.MODEL;
        return objectFromRawResource(context, name);
    }

    private GObject objectFromDefaultOnyxConfig(Context context) {
        return objectFromRawResource(context, "onyx");
    }

    private GObject objectFromRawResource(Context context, final String name) {
        GObject object = null;
        try {
            int res = context.getResources().getIdentifier(name.toLowerCase(), "raw", context.getPackageName());
            object = RawResourceUtil.objectFromRawResource(context, res);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return object;
        }
    }

}
