package com.onyx.einfo.model;

import android.content.Context;
import android.content.Intent;

import com.onyx.einfo.utils.Constant;
import com.onyx.einfo.manager.ConfigPreferenceManager;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;

/**
 * Created by suicheng on 2016/11/26.
 */
public class AccountInfo extends NeoAccountBase {
    private final static String UPDATE_STATUS_BAR_INFO_ACTION = "update_status_bar_info";
    private final static String ARGS_NAME = "args_name";
    private final static String ARGS_ORGANIZATION_INFO = "args_organization_info";

    public static String loadAvatarPath(Context context) {
        return ConfigPreferenceManager.getStringValue(context, Constant.ACCOUNT_AVATAR, "");
    }

    public static void saveAvatarPath(Context context, String path) {
        ConfigPreferenceManager.setStringValue(context, Constant.ACCOUNT_AVATAR, path);
    }

    public static void sendUserInfoSettingIntent(Context context, NeoAccountBase account) {
        if (!NeoAccountBase.isValid(account)) {
            return;
        }
        Intent intent = new Intent(UPDATE_STATUS_BAR_INFO_ACTION);
        intent.putExtra(ARGS_NAME, account.getName());
        intent.putExtra(ARGS_ORGANIZATION_INFO, account.getFirstGroup());
        context.sendBroadcast(intent);
    }

    public static void sendUserInfoSettingIntent(Context context, String name) {
        Intent intent = new Intent(UPDATE_STATUS_BAR_INFO_ACTION);
        intent.putExtra(ARGS_NAME, name);
        context.sendBroadcast(intent);
    }
}
