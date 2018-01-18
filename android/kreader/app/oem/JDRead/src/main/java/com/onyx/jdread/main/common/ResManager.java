package com.onyx.jdread.main.common;

import android.content.res.TypedArray;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;

/**
 * Created by li on 2018/1/18.
 */

public class ResManager {

    public static String getResString(int resId) {
        return JDReadApplication.getInstance().getResources().getString(resId);
    }

    public static Integer getResInteger(int resId) {
        return JDReadApplication.getInstance().getResources().getInteger(resId);
    }

    public static String[] getResStringArray(int resId) {
        return JDReadApplication.getInstance().getResources().getStringArray(resId);
    }

    public static TypedArray getTypedArray(int resId) {
        return JDReadApplication.getInstance().getResources().obtainTypedArray(resId);
    }
}
