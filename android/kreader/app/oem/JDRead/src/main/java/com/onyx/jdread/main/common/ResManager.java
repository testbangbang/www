package com.onyx.jdread.main.common;

import android.content.Context;
import android.content.res.TypedArray;

import com.onyx.jdread.JDReadApplication;

/**
 * Created by li on 2018/1/18.
 */

public class ResManager {
    private static Context context;
    private static ResManager resManager;

    public static void init(Context context) {
        ResManager.context = context;
    }

    public static ResManager getResManager() {
        if (resManager == null) {
            resManager = new ResManager();
        }
        return resManager;
    }

    public String getString(int resId) {
        return context.getResources().getString(resId);
    }

    public Integer getInteger(int resId) {
        return context.getResources().getInteger(resId);
    }

    public String[] getStringArray(int resId) {
        return context.getResources().getStringArray(resId);
    }

    public TypedArray getTypedArray(int resId) {
        return context.getResources().obtainTypedArray(resId);
    }
}
