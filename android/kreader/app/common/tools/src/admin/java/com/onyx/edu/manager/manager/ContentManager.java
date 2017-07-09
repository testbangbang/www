package com.onyx.edu.manager.manager;

import android.content.Context;

import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by suicheng on 2017/6/16.
 */
public class ContentManager extends PreferenceManager {
    public static final String KEY_ACCOUNT = "key_account";

    public static final String KEY_GROUP_USER_INFO = "key_group_user_info";
    public static final String KEY_GROUP_USER_INFO_FROM = "key_group_user_info_from";

    public static void saveAccount(Context context, NeoAccountBase accountInfo) {
        if (accountInfo != null) {
            accountInfo.beforeSave();
        }
        setStringValue(context, KEY_ACCOUNT, StringUtils.getBlankStr(JSONObjectParseUtils.toJson(accountInfo)));
    }

    public static NeoAccountBase getAccount(Context context) {
        return JSONObjectParseUtils.parseObject(getStringValue(context, KEY_ACCOUNT, ""),
                NeoAccountBase.class);
    }
}
