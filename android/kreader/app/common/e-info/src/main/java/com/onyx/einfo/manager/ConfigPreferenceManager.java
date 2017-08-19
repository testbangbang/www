package com.onyx.einfo.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.einfo.utils.Constant;

/**
 * Created by suicheng on 2016/11/18.
 */
public class ConfigPreferenceManager extends PreferenceManager {

    public static boolean hasImportContent(Context context) {
        return getBooleanValue(context, Constant.IMPORT_CONTENT_IN_FIRST_BOOT_TAG, false);
    }

    public static void setImportContent(Context context, boolean imported) {
        setBooleanValue(context, Constant.IMPORT_CONTENT_IN_FIRST_BOOT_TAG, imported);
    }

    public static Intent getSettingsIntent() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.onyx.android.settings",
                "com.onyx.android.libsetting.view.activity.DeviceMainSettingActivity"));
        return intent;
    }
}
