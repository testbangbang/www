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
    public static final String SP_KEY_CATEGORY_LEVEL_ONE_ID = "category_level_one_id";
    public static final String SP_KEY_CATEGORY_LEVEL_TWO_ID = "category_level_two_id";
    public static final String SP_KEY_CATEGORY_NAME = "category_name";
    public static final String SP_KEY_CATEGORY_ISFREE = "category_isfree";
    public static final String SP_KEY_SUBJECT_NAME = "subject_name";
    public static final String SP_KEY_SUBJECT_MODEL_ID = "subject_model_id";
    public static final String SP_KEY_SUBJECT_MODEL_TYPE = "subject_model_type";
    public static final String SP_KEY_SUBJECT_RANK_TYPE = "subject_rank_type";
    public static final String SP_KEY_KEYWORD = "Keyword";
    public static final String SP_KEY_BOOK_LIST_TYPE = "book_list_type";
    public static final String SP_KEY_SEARCH_BOOK_CAT_ID = "book_search_book_cat_id";

    public static final int BOOK_SHOP_DEFAULT_CID = 11;
    public static final int BOOK_COMMENT_PAGE_SIZE = 20;
    public static final int BOOK_CATEGORY_PAGE_SIZE = 20;
    public static final String BOOK_PAGE_SIZE = "20";
    public static final int PAGE_STEP = 1;
    public static final String BOOK_FORMAT = ".JEB";
    public static final String RESULT_CODE_SUCCESS = "0";
    public static final String RESULT_CODE_UNKNOWN_ERROR = "1";
    public static final String RESULT_CODE_NO_FUNCTION = "2";
    public static final String RESULT_CODE_NOT_LOGIN = "3";
    public static final String RESULT_CODE_PARAMS_ERROR = "4";
    public static final String RESULT_CODE_PARAMS_LENGTH_ERROR = "5";
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
    public static final String ENCRYPTION_DIR = "drdrop";
    public static final String ENCRYPTION_NAME = "dataread.jdr";
    public static final int SHOP_MAIN_INDEX_ZERO = 0;
    public static final int SHOP_MAIN_INDEX_ONE = 1;
    public static final int SHOP_MAIN_INDEX_TWO = 2;
    public static final int SHOP_MAIN_INDEX_THREE = 3;
    public static final int SHOP_MAIN_INDEX_FOUR = 4;
    public static final int SHOP_MAIN_INDEX_FIVE = 5;
    public static final int SHOP_MAIN_INDEX_SIX = 6;
    public static final int SHOP_MAIN_INDEX_SEVEN = 7;
    public static final int SHOP_MAIN_INDEX_EIGHT = 8;
    public static final int SHOP_MAIN_INDEX_NINE = 9;
    public static final int SHOP_MAIN_INDEX_TEN = 10;
    public static final int SHOP_MAIN_INDEX_ELEVEN = 11;
    public static final int SHOP_MAIN_INDEX_TWELVE = 12;
    public static final int CATEGORY_LEVEL_ONE = 1;
    public static final int CATEGORY_LEVEL_TWO = 2;
    public static final int RANK_LIST_SIZE = 6;
    public static final long RESET_PRESS_TIMEOUT = 1000;
    public static final int START_PRODUCTION_TEST_PRESS_COUNT = 6;
    public static final int REMOVE_PRODUCTION_TEST_PRESS_COUNT = 5;
    public static final int BOOK_LIST_TYPE_BOOK_MODEL = 1;
    public static final int BOOK_LIST_TYPE_BOOK_RANK = 2;
    public static final int WEB_VIEW_TEXT_ZOOM = 100;
}
