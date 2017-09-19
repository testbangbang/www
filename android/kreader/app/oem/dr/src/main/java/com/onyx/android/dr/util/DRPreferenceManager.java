package com.onyx.android.dr.util;

import android.content.Context;

import com.onyx.android.dr.common.Constants;
import com.onyx.android.sdk.utils.PreferenceManager;

/**
 * Created by suicheng on 2016/11/18.
 */
public class DRPreferenceManager extends PreferenceManager {
    public static void saveGradeSelected(Context context, String gradleSelected) {
        setStringValue(context, Constants.CONFIG_STUDENT_GRADE, gradleSelected);
    }

    public static String loadGradeSelected(Context context, String defaultValue) {
        return getStringValue(context, Constants.CONFIG_STUDENT_GRADE, defaultValue);
    }

    public static void savePublisherSelected(Context context, String publisherSelected) {
        setStringValue(context, Constants.CONFIG_MATERIAL_PUBLISHER, publisherSelected);
    }

    public static String loadPublisherSelected(Context context, String defaultValue) {
        return getStringValue(context, Constants.CONFIG_MATERIAL_PUBLISHER, defaultValue);
    }

    public static void saveSchoolSelected(Context context, String schoolSelected) {
        setStringValue(context, Constants.CONFIG_SCHOOL_LEVEL, schoolSelected);
    }

    public static String loadSchoolSelected(Context context, String defaultValue) {
        return getStringValue(context, Constants.CONFIG_SCHOOL_LEVEL, defaultValue);
    }

    public static void saveLibraryParentId(Context context, String libraryParentId) {
        setStringValue(context, Constants.LIBRARY_PARENT_ID, libraryParentId);
    }

    public static String loadLibraryParentId(Context context, String defaultValue) {
        return getStringValue(context, Constants.LIBRARY_PARENT_ID, defaultValue);
    }

    public static void saveParentId(Context context, String libraryParentId) {
        setStringValue(context, Constants.PARENT_ID, libraryParentId);
    }

    public static String loadParentId(Context context, String defaultValue) {
        return getStringValue(context, Constants.PARENT_ID, defaultValue);
    }

    public static boolean hasImportContent(Context context) {
        return getBooleanValue(context, Constants.IMPORT_CONTENT_IN_FIRST_BOOT_TAG, false);
    }

    public static void setImportContent(Context context, boolean imported) {
        setBooleanValue(context, Constants.IMPORT_CONTENT_IN_FIRST_BOOT_TAG, imported);
    }

    public static void saveUserAccount(Context context, String userName) {
        setStringValue(context, Constants.ACCOUNT, userName);
    }

    public static String getUserAccount(Context context, String defaultValue) {
        return getStringValue(context, Constants.ACCOUNT, defaultValue);
    }

    public static void saveUserPassword(Context context, String password) {
        setStringValue(context, Constants.PASSWORD, password);
    }

    public static String getUserPassword(Context context, String defaultValue) {
        return getStringValue(context, Constants.PASSWORD, defaultValue);
    }

    public static void saveUserType(Context context, String userType) {
        setStringValue(context, Constants.USER_TYPE, userType);
    }

    public static String getUserType(Context context, String defaultValue) {
        return getStringValue(context, Constants.USER_TYPE, defaultValue);
    }

    public static String loadPreferredBookshelf(Context context, String defaultValue) {
        return getStringValue(context, Constants.PREFERRED_BOOKSHELF, defaultValue);
    }

    public static void saveAutoLogin(Context context, boolean isAutoLogin) {
        setBooleanValue(context, Constants.AUTO_LOGIN_FLAG, isAutoLogin);
    }

    public static String getBookshelfType(Context context, String defaultValue) {
        return getStringValue(context, Constants.BOOKSHELF_TYPE, defaultValue);
    }

    public static void saveBookshelfType(Context context, String bookshelfType) {
        setStringValue(context, Constants.BOOKSHELF_TYPE, bookshelfType);
    }

    public static String getSearchKeyword(Context context, String defaultValue) {
        return getStringValue(context, Constants.SEARCH_KEYWORD, defaultValue);
    }

    public static void saveSearchKeyword(Context context, String bookshelfType) {
        setStringValue(context, Constants.SEARCH_KEYWORD, bookshelfType);
    }
}
