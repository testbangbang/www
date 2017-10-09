package com.onyx.android.sdk.data.provider;

import android.content.Context;
import android.net.Uri;

import com.onyx.android.sdk.data.db.table.OnyxSystemConfigProvider;
import com.onyx.android.sdk.data.model.v2.SystemKeyValueItem;
import com.onyx.android.sdk.data.model.v2.SystemKeyValueItem_Table;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

/**
 * Created by suicheng on 2017/6/19.
 */
public class SystemConfigProvider {
    public static final int INT_INVALID = -1;
    public static final String KEY_CONTENT_SERVER_INFO = "sys.content_server_info";
    public static final String KEY_APP_PREFERENCE = "sys.app_preference";
    public static final String KEY_STORAGE_FOLDER_SHORTCUT_LIST = "sys.storage_shortcut_list";

    public static String getStringValue(Context context, String key) {
        SystemKeyValueItem item = queryKeyValueItem(context, key);
        return item == null ? null : item.value;
    }

    public static boolean setStringValue(Context context, String key, String value) {
        return addKeyValueItem(context, SystemKeyValueItem.create(key, value));
    }

    public static boolean setIntValue(Context context, String key, int value) {
        return setStringValue(context, key, String.valueOf(value));
    }

    public static int getIntValue(Context context, String key) {
        String value = getStringValue(context, key);
        if (value == null) {
            return INT_INVALID;
        }
        return parseInteger(value);
    }

    public static boolean getBooleanValue(Context context, String key) {
        String value = getStringValue(context, key);
        if (value == null) {
            return false;
        }
        return parseBoolean(value);
    }

    public static boolean setBooleanValue(Context context, String key, boolean value) {
        return setStringValue(context, key, String.valueOf(value));
    }

    public static SystemKeyValueItem queryKeyValueItem(Context context, String key) {
        try {
            return ContentUtils.querySingle(OnyxSystemConfigProvider.CONTENT_URI, SystemKeyValueItem.class,
                    ConditionGroup.clause().and(SystemKeyValueItem_Table.key.eq(key)), null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean addKeyValueItem(Context context, SystemKeyValueItem item) {
        item.beforeSave();
        SystemKeyValueItem findItem = queryKeyValueItem(context, item.key);
        long result = 0;
        if (findItem == null || !findItem.hasValidId()) {
            Uri uri = ContentUtils.insert(OnyxSystemConfigProvider.CONTENT_URI, item);
            if (uri == null || StringUtils.isNullOrEmpty(uri.getLastPathSegment())) {
                result = INT_INVALID;
            } else {
                result = parseLong(uri.getLastPathSegment());
            }
        } else {
            item.setId(findItem.getId());
            result = ContentUtils.update(OnyxSystemConfigProvider.CONTENT_URI, item);
        }
        if (result > 0) {
            item.setId(result);
        }
        return result > 0;
    }

    public static boolean delete(Context context, SystemKeyValueItem item) {
        int rowNumber = ContentUtils.delete(OnyxSystemConfigProvider.CONTENT_URI, item);
        return rowNumber > 0;
    }

    private static int parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return INT_INVALID;
        }
    }

    private static long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return INT_INVALID;
        }
    }

    private static boolean parseBoolean(String value) {
        try {
            return Boolean.parseBoolean(value);
        } catch (Exception e) {
            return false;
        }
    }
}
