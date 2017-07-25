package com.onyx.einfo.utils;

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
        setStringValue(context, Constant.CONFIG_MATERIAL_PUBLISHER, publisherSelected);
    }

    public static String loadPublisherSelected(Context context, String defaultValue) {
        return getStringValue(context, Constant.CONFIG_MATERIAL_PUBLISHER, defaultValue);
    }

    public static void saveSchoolSelected(Context context, String schoolSelected) {
        setStringValue(context, Constant.CONFIG_SCHOOL_LEVEL, schoolSelected);
    }

    public static String loadSchoolSelected(Context context, String defaultValue) {
        return getStringValue(context, Constant.CONFIG_SCHOOL_LEVEL, defaultValue);
    }

    public static void saveLibraryParentId(Context context, String libraryParentId) {
        setStringValue(context, Constant.LIBRARY_PARENT_ID, libraryParentId);
    }

    public static String loadLibraryParentId(Context context, String defaultValue) {
        return getStringValue(context, Constant.LIBRARY_PARENT_ID, defaultValue);
    }

    public static boolean hasImportContent(Context context) {
        return getBooleanValue(context, Constant.IMPORT_CONTENT_IN_FIRST_BOOT_TAG, false);
    }

    public static void setImportContent(Context context, boolean imported) {
        setBooleanValue(context, Constant.IMPORT_CONTENT_IN_FIRST_BOOT_TAG, imported);
    }
}
