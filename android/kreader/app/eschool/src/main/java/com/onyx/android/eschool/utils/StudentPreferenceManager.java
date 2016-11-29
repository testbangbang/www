package com.onyx.android.eschool.utils;

import android.content.Context;

import com.onyx.android.sdk.utils.PreferenceManager;

/**
 * Created by suicheng on 2016/11/18.
 */
public class StudentPreferenceManager extends PreferenceManager {

    public static void saveGradeSelected(Context context, String gradleSelected) {
        setStringValue(context, Constant.CONFIG_STUDENT_GRADE, gradleSelected);
    }

    public static String loadGradeSelected(Context context, String defaultValue) {
        return getStringValue(context, Constant.CONFIG_STUDENT_GRADE, defaultValue);
    }

    public static void savePublisherSelected(Context context, String publisherSelected) {
        com.onyx.android.sdk.utils.PreferenceManager.setStringValue(context, Constant.CONFIG_MATERIAL_PUBLISHER, publisherSelected);
    }

    public static String loadPublisherSelected(Context context, String defaultValue) {
        return getStringValue(context, Constant.CONFIG_MATERIAL_PUBLISHER, defaultValue);
    }

    public static void saveSchoolSelected(Context context, String schoolSelected) {
        com.onyx.android.sdk.utils.PreferenceManager.setStringValue(context, Constant.CONFIG_SCHOOL_LEVEL, schoolSelected);
    }

    public static String loadSchoolSelected(Context context, String defaultValue) {
        return getStringValue(context, Constant.CONFIG_SCHOOL_LEVEL, defaultValue);
    }
}
