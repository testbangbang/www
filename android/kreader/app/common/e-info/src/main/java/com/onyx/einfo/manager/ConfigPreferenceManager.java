package com.onyx.einfo.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.ViewType;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.einfo.R;
import com.onyx.einfo.utils.Constant;

/**
 * Created by suicheng on 2016/11/18.
 */
public class ConfigPreferenceManager extends PreferenceManager {
    public static final String VIEW_TYPE_TAG = "viewType";
    public static final String CLOUD_SORT_BY_TAG = "cloud_sort_by";
    public static final String CLOUD_SORT_ORDER_TAG = "cloud_sort_order";

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

    public static ViewType getViewType(Context context) {
        return ViewType.valueOf(getStringValue(context, VIEW_TYPE_TAG, ViewType.Thumbnail.toString()));
    }

    public static void setViewType(Context context, ViewType viewType) {
        setStringValue(context, VIEW_TYPE_TAG, viewType.toString());
    }

    public static SortBy getCloudSortBy(Context context) {
        return SortBy.valueOf(getStringValue(context, CLOUD_SORT_BY_TAG,
                SortBy.Ordinal.toString()));
    }

    public static void setCloudSortBy(Context context, SortBy sortBy) {
        setStringValue(context, CLOUD_SORT_BY_TAG, sortBy.toString());
    }

    public static SortOrder getCloudSortOrder(Context context) {
        return SortOrder.valueOf(getStringValue(context, CLOUD_SORT_ORDER_TAG,
                SortOrder.Asc.toString()));
    }

    public static void setCloudSortOrder(Context context, SortOrder order) {
        setStringValue(context, CLOUD_SORT_ORDER_TAG, order.toString());
    }
}
