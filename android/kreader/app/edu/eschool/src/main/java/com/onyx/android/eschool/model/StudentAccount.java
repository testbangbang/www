package com.onyx.android.eschool.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.onyx.android.eschool.utils.Constant;
import com.onyx.android.eschool.utils.StudentPreferenceManager;
import com.onyx.android.sdk.data.model.ContentAccount;
import com.onyx.android.sdk.data.model.SecurePreferences;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by suicheng on 2016/11/26.
 */

public class StudentAccount {

    public static final String DELIMITER = ",";

    public String name;
    public String school;
    public String gradeClass;
    public String grade;
    public String studentId;

    public String[] groups;
    public String phone;
    public String token;
    public ContentAccount accountInfo;

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

    public void saveAccount(Context context) {
        SecurePreferences preferences = new SecurePreferences(context, Constant.ACCOUNT_TYPE_STUDENT, Constant.ACCOUNT_INFO_TAG, true);
        preferences.put(Constant.JSON_TAG, JSON.toJSONString(this));
    }

    public static StudentAccount loadAccount(Context context) {
        try {
            SecurePreferences preferences = new SecurePreferences(context, Constant.ACCOUNT_TYPE_STUDENT, Constant.ACCOUNT_INFO_TAG, true);
            final String string = preferences.getString(Constant.JSON_TAG);
            if (StringUtils.isNullOrEmpty(string)) {
                return new StudentAccount();
            }
            return JSON.parseObject(string, StudentAccount.class);
        } catch (Exception e) {
            return new StudentAccount();
        }
    }

    public static String loadAvatarPath(Context context) {
        return StudentPreferenceManager.getStringValue(context, Constant.ACCOUNT_AVATAR, "");
    }

    public static void saveAvatarPath(Context context, String path) {
        StudentPreferenceManager.setStringValue(context, Constant.ACCOUNT_AVATAR, path);
    }

    public static boolean isAccountValid(Context context) {
        StudentAccount account = StudentAccount.loadAccount(context);
        return account != null && StringUtils.isNotBlank(account.token) && account.accountInfo != null;
    }
}
