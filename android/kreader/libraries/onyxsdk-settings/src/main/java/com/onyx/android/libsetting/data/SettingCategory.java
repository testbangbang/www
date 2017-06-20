package com.onyx.android.libsetting.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by solskjaer49 on 2016/11/29 12:25.
 */

public class SettingCategory {
    public static final int UNKNOWN = -1;
    public static final int NETWORK = 0;
    public static final int USER_SETTING = 1;
    public static final int SOUND = 2;
    public static final int STORAGE = 3;
    public static final int LANGUAGE_AND_INPUT = 4;
    public static final int DATE_TIME_SETTING = 5;
    public static final int APPLICATION_MANAGEMENT = 6;
    public static final int POWER = 7;
    public static final int SECURITY = 8;
    public static final int ERROR_REPORT = 9;

    static public final String SETTING_ITEM_NETWORK_TAG = "setting_item_network";
    static public final String SETTING_ITEM_USER_SETTING_TAG = "setting_item_user_setting";
    static public final String SETTING_ITEM_POWER_TAG = "setting_item_power";
    static public final String SETTING_ITEM_LANG_INPUT_TAG = "setting_item_lang_input";
    static public final String SETTING_ITEM_DATE_TIME_TAG = "setting_item_date_time";
    static public final String SETTING_ITEM_APPLICATION_TAG = "setting_item_application";
    static public final String SETTING_ITEM_STORAGE_TAG = "setting_item_storage";
    static public final String SETTING_ITEM_SECURITY_TAG = "setting_item_security";
    static public final String SETTING_ITEM_ERROR_REPORT_TAG = "setting_item_error_report";

    // ... type definitions
    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({UNKNOWN, NETWORK, USER_SETTING, SOUND, STORAGE,
            LANGUAGE_AND_INPUT, DATE_TIME_SETTING, APPLICATION_MANAGEMENT, POWER, SECURITY, ERROR_REPORT})
    // Create an interface for validating int types
    public @interface SettingCategoryDef {
    }

    public
    @SettingCategoryDef
    static int translate(int val) {
        return val;
    }

    public
    @SettingCategoryDef
    static int translate(String tag) {
        switch (tag) {
            case SETTING_ITEM_NETWORK_TAG:
                return NETWORK;
            case SETTING_ITEM_USER_SETTING_TAG:
                return USER_SETTING;
            case SETTING_ITEM_POWER_TAG:
                return POWER;
            case SETTING_ITEM_LANG_INPUT_TAG:
                return LANGUAGE_AND_INPUT;
            case SETTING_ITEM_DATE_TIME_TAG:
                return DATE_TIME_SETTING;
            case SETTING_ITEM_APPLICATION_TAG:
                return APPLICATION_MANAGEMENT;
            case SETTING_ITEM_STORAGE_TAG:
                return STORAGE;
            case SETTING_ITEM_SECURITY_TAG:
                return SECURITY;
            case SETTING_ITEM_ERROR_REPORT_TAG:
                return ERROR_REPORT;
        }
        return UNKNOWN;
    }
}
