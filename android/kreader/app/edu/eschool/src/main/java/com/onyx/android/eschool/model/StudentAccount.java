package com.onyx.android.eschool.model;

import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.onyx.android.eschool.utils.Constant;
import com.onyx.android.eschool.utils.StudentPreferenceManager;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.model.SecurePreferences;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by suicheng on 2016/11/26.
 */

public class StudentAccount extends NeoAccountBase {
    private final static String UPDATE_STATUS_BAR_INFO_ACTION = "update_status_bar_info";
    private final static String ARGS_NAME = "args_name";
    private final static String ARGS_ORGANIZATION_INFO = "args_organization_info";

    public static final String DELIMITER = ",";

    public String name;
    public String school;
    public String gradeClass;
    public String grade;
    public String studentId;

    public String[] groups;
    public String phone;

    private static StudentAccount currentAccount;

    @JSONField(deserialize=false)
    public String getFirstGroup() {
        if (groups == null || groups.length <= 0) {
            return "";
        }
        return groups[0].replaceAll(StudentAccount.DELIMITER, "");
    }

    public String getPhone() {
        return StringUtils.getBlankStr(phone);
    }

    public String getName() {
        return StringUtils.getBlankStr(name);
    }

    public static StudentAccount currentAccount(Context context) {
        if (currentAccount == null) {
            currentAccount = loadAccount(context);
        }
        return currentAccount;
    }

    public void saveAccount(Context context) {
        SecurePreferences preferences = new SecurePreferences(context, Constant.ACCOUNT_TYPE_STUDENT, Constant.ACCOUNT_INFO_TAG, true);
        preferences.put(Constant.JSON_TAG, JSON.toJSONString(this));
    }

    public static void saveAccount(Context context, StudentAccount account) {
        if (account == null) {
            return;
        }
        currentAccount = account;
        account.saveAccount(context);
    }

    public static StudentAccount loadAccount(Context context) {
        SecurePreferences preferences = new SecurePreferences(context, Constant.ACCOUNT_TYPE_STUDENT, Constant.ACCOUNT_INFO_TAG, true);
        final String string = preferences.getString(Constant.JSON_TAG);
        if (StringUtils.isNullOrEmpty(string)) {
            return  currentAccount = new StudentAccount();
        }
        currentAccount = JSONObjectParseUtils.parseObject(string, StudentAccount.class);
        if (currentAccount == null) {
            currentAccount = new StudentAccount();
        }
        return currentAccount;
    }

    public static String loadAvatarPath(Context context) {
        return StudentPreferenceManager.getStringValue(context, Constant.ACCOUNT_AVATAR, "");
    }

    public static void saveAvatarPath(Context context, String path) {
        StudentPreferenceManager.setStringValue(context, Constant.ACCOUNT_AVATAR, path);
    }

    public static boolean isAccountValid(Context context) {
        StudentAccount account = StudentAccount.loadAccount(context);
        return isAccountValid(context, account);
    }

    public static boolean isAccountValid(Context context, StudentAccount account) {
        return account != null && StringUtils.isNotBlank(account.token);
    }

    public static void sendUserInfoSettingIntent(Context context, NeoAccountBase account) {
        if (account == null) {
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
