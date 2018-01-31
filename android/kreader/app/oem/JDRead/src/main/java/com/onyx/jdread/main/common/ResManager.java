package com.onyx.jdread.main.common;

import android.content.Context;
import android.content.res.TypedArray;

/**
 * Created by li on 2018/1/18.
 */

public class ResManager {
    private static Context context;

    public static void init(Context context) {
        ResManager.context = context;
    }

    public static String getString(int resId) {
        return context.getResources().getString(resId);
    }

    public static Integer getInteger(int resId) {
        return context.getResources().getInteger(resId);
    }

    public static String[] getStringArray(int resId) {
        return context.getResources().getStringArray(resId);
    }

    public static TypedArray getTypedArray(int resId) {
        return context.getResources().obtainTypedArray(resId);
    }

    public static int getColor(int res){
        return context.getResources().getColor(res);
    }
}
