package com.onyx.android.plato.common;

import com.onyx.android.sdk.device.Device;

import java.io.File;

/**
 * Created by li on 2017/9/30.
 */

public class Constants {
    public static final int VALUE_ZERO = 0;
    public static final int VALUE_ONE = 1;
    public static final int VALUE_NEGATIVE_ONE = -1;
    public static final String MD_5 = "MD5";
    public static final String UTF_8 = "UTF-8";
    public static final String POST = "POST";
    public static final String QUESTION_ID = "question_id";
    public static final String QUESTION_TITLE = "question_title";
    public static final String QUESTION_TAG = "question_tag";
    public static final String APK_NAME = "Sun.apk";
    public static final String APK_DOWNLOAD_PATH = Device.currentDevice.getExternalStorageDirectory() + File.separator + Constants.APK_NAME;
    public static final String UPDATE_URL = "update_url";
    public static final String UPDATE_ZIP = "update.zip";
    public static boolean isUseCache = true;
    public static final String QUESTION_TYPE_CHOICE = "选择题";
    public static final String QUESTION_TYPE_OBJECTIVE = "阅读理解";
    public static final String QUESTION_TYPE_BLANK = "填空题";
    public static final long RESET_PRESS_TIMEOUT = 1000;
    public static final int SYSTEM_SETTING_PRESS_COUNT = 6;

    //SharedPreferences constant
    public static final String SP_NAME_USERINFO = "userinfo";
    public static final String SP_KEY_ISKEEPPASSWORD = "isKeepPassword";
    public static final String SP_KEY_USER_ACCOUNT = "userAccount";
    public static final String SP_KEY_USER_PASSWORD = "userPassword";
    public static final String SOUND_DIR = "sounds";
    public static final String REQUEST_HEAD = "application/json";
    public static final String OK = "ok";
    public static final String SP_KEY_USER_NAME = "userName";
    public static final String SP_KEY_USER_PHONE_NUMBER = "userPhoneNumber";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_PAGE = "1";
    public static final String CORRECT_TYPE = "correct";
    public static final String TASK = "task";
    public static final String EXAM = "exam";
    public static final String P_MARK = "</p>";
    public static final String VOICE_FORMAT = ".amr";
}
