package com.onyx.einfo.manager;

import android.content.Context;

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
}
