package com.onyx.android.libsetting.data;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.widget.Toast;

import com.onyx.android.libsetting.SettingConfig;
import com.onyx.android.libsetting.view.activity.ApplicationSettingActivity;
import com.onyx.android.libsetting.view.activity.DateTimeSettingActivity;
import com.onyx.android.libsetting.view.activity.FirmwareOTAActivity;
import com.onyx.android.libsetting.view.activity.LanguageInputSettingActivity;
import com.onyx.android.libsetting.view.activity.NetworkSettingActivity;
import com.onyx.android.libsetting.view.activity.PowerSettingActivity;
import com.onyx.android.libsetting.view.activity.ProductDetailSettingActivity;
import com.onyx.android.libsetting.view.activity.SecuritySettingActivity;
import com.onyx.android.libsetting.view.activity.UserSettingActivity;
import com.onyx.android.libsetting.view.activity.WifiSettingActivity;

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
    public static final int PRODUCTION_TEST = 10;
    public static final int WIFI = 11;
    public static final int BLUETOOTH = 12;
    public static final int FIRMWARE_UPDATE = 13;
    public static final int PRODUCT_DETAIL = 14;

    static public final String SETTING_ITEM_NETWORK_TAG = "setting_item_network";
    static public final String SETTING_ITEM_USER_SETTING_TAG = "setting_item_user_setting";
    static public final String SETTING_ITEM_POWER_TAG = "setting_item_power";
    static public final String SETTING_ITEM_LANG_INPUT_TAG = "setting_item_lang_input";
    static public final String SETTING_ITEM_DATE_TIME_TAG = "setting_item_date_time";
    static public final String SETTING_ITEM_APPLICATION_TAG = "setting_item_application";
    static public final String SETTING_ITEM_STORAGE_TAG = "setting_item_storage";
    static public final String SETTING_ITEM_SECURITY_TAG = "setting_item_security";
    static public final String SETTING_ITEM_ERROR_REPORT_TAG = "setting_item_error_report";
    static public final String SETTING_ITEM_PRODUCTION_TEST_TAG = "setting_item_production_test";
    static public final String SETTING_ITEM_WIFI_TAG = "setting_item_wifi";
    static public final String SETTING_ITEM_BLUETOOTH_TAG = "setting_item_bluetooth";
    static public final String SETTING_ITEM_FIRMWARE_UPDATE_TAG = "setting_item_firmware_update";
    static public final String SETTING_ITEM_PRODUCT_DETAIL_TAG = "setting_item_product_detail";

    // ... type definitions
    // Describes when the annotation will be discarded
    @Retention(RetentionPolicy.SOURCE)
    // Enumerate valid values for this interface
    @IntDef({UNKNOWN, NETWORK, USER_SETTING, SOUND, STORAGE,
            LANGUAGE_AND_INPUT, DATE_TIME_SETTING, APPLICATION_MANAGEMENT, POWER,
            SECURITY, ERROR_REPORT, PRODUCTION_TEST, WIFI, BLUETOOTH, FIRMWARE_UPDATE,PRODUCT_DETAIL})
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
            case SETTING_ITEM_PRODUCTION_TEST_TAG:
                return PRODUCTION_TEST;
            case SETTING_ITEM_WIFI_TAG:
                return WIFI;
            case SETTING_ITEM_BLUETOOTH_TAG:
                return BLUETOOTH;
            case SETTING_ITEM_FIRMWARE_UPDATE_TAG:
                return FIRMWARE_UPDATE;
            case SETTING_ITEM_PRODUCT_DETAIL_TAG:
                return PRODUCT_DETAIL;
        }
        return UNKNOWN;
    }

    public static Intent getConfigIntentByCategory(Context context, @SettingCategoryDef int itemCategory) {
        Intent intent = null;
        SettingConfig config = SettingConfig.sharedInstance(context);
        switch (itemCategory) {
            case SettingCategory.NETWORK:
                intent = new Intent(context, NetworkSettingActivity.class);
                break;
            case SettingCategory.SECURITY:
                intent = new Intent(context, SecuritySettingActivity.class);
                break;
            case SettingCategory.STORAGE:
                intent = config.getStorageSettingIntent(context);
                break;
            case SettingCategory.LANGUAGE_AND_INPUT:
                intent = new Intent(context, LanguageInputSettingActivity.class);
                break;
            case SettingCategory.DATE_TIME_SETTING:
                intent = new Intent(context, DateTimeSettingActivity.class);
                break;
            case SettingCategory.POWER:
                intent = new Intent(context, PowerSettingActivity.class);
                break;
            case SettingCategory.APPLICATION_MANAGEMENT:
                intent = new Intent(context, ApplicationSettingActivity.class);
                break;
            case SettingCategory.USER_SETTING:
                intent = new Intent(context, UserSettingActivity.class);
                break;
            case SettingCategory.ERROR_REPORT:
                if (!TextUtils.isEmpty(config.getErrorReportAction())) {
                    intent = new Intent(config.getErrorReportAction());
                }
                break;
            case SettingCategory.PRODUCTION_TEST:
                intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setClassName("com.onyx.android.production.test", "com.onyx.android.productiontest.activity.ProductionTestMainActivity");
                break;
            case SettingCategory.WIFI:
                intent = new Intent(context, WifiSettingActivity.class);
                break;
            case SettingCategory.BLUETOOTH:
                intent = config.getBluetoothSettingIntent();
                break;
            case SettingCategory.FIRMWARE_UPDATE:
                intent = new Intent(context, FirmwareOTAActivity.class);
                break;
            case SettingCategory.PRODUCT_DETAIL:
                intent = new Intent(context, ProductDetailSettingActivity.class);
                break;
            default:
                Toast.makeText(context, "Under Construction", Toast.LENGTH_SHORT).show();
                return null;
        }
        return intent;
    }
}
