package com.onyx.jdread.main.common;

import android.os.Environment;

/**
 * Created by 12 on 2016/12/6.
 */

public class Constants {
    public static final String COOKIE_KEY = "wskey";
    public static final String COOKIE_DOMAIN = ".jd.com";
    public static final String UTF_8 = "UTF-8";
    public static final String CLIENT_VERSION = "2.6.3";
    public static boolean isUseCache = true;
    public static final String POST = "POST";
    public static final String MD_5 = "MD5";

    public static final String SP_KEY_BOOK_ID = "book_id";
    public final static String SP_KEY_ACCOUNT = "user_account";
    public final static String SP_KEY_PASSWORD = "user_password";
    public static final String SP_KEY_USER_IMAGE_URL = "user_image_url";
    public static final String SP_KEY_USER_NICK_NAME = "user_nick_name";
    public static final String SP_KEY_USER_NAME = "user_name";
    public static final String SP_KEY_LIST_PIN = "listPin";
    public static final String SP_KEY_SHOW_PASSWORD = "show_password";
    public static final String SP_KEY_CATEGORY_ID = "category_id";
    public static final String SP_KEY_CATEGORY_NAME = "category_name";
    public static final String SP_KEY_CATEGORY_ISFREE = "category_isfree";

    public static final int BOOK_COMMENT_PAGE_SIZE = 20;
    public static final int BOOK_CATEGORY_PAGE_SIZE = 20;
    public static final int PAGE_STEP = 1;
    public static final String BOOK_FORMAT = ".JEB";
    public static final String LOGIN_CODE_SUCCESS = "0";
    public static final String LOGIN_CODE_PARAMS_ERROR = "1";
    public static final String LOGIN_CODE_NO_FUNCTION = "2";
    public static final String LOGIN_CODE_NOT_LOGIN = "3";
    public static final String LOGIN_CODE_SERVER_ERROR_CODE_ONE = "-1";
    public static final String LOGIN_CODE_SERVER_ERROR_CODE_TWO = "-2";
    public static final int PASSWORD_MIN_LENGTH = 6;
    public static final String CODE_STATE_THREE = "3";
    public static final String CODE_STATE_FOUR = "4";
    public static final String CART_TYPE_GET = "1";
    public static final String CART_TYPE_ADD = "2";
    public static final String CART_TYPE_DEL = "3";
    public static final String RANDOW_VALUE = "0";
    public static final String HAS_CERT_VALUE = "0";
    public static final int ORDER_TYPE = 0;
    public static final String DEVICE_TYPE_A = "A";
    public static final String PAY_URL = "pay_url";
    public final static long APP_CACHE_MAX_SIZE = 1024 * 1024 * 8;
    public final static String LOCAL_WEB_CACHE_PATH = Environment.getExternalStorageDirectory().getPath() + "/webcache";
    public static final int CATEGORY_TYPE_FREE = 1;
}
