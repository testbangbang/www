package com.onyx.android.dr.common;

import com.onyx.android.sdk.device.Device;

import java.io.File;

/**
 * Created by hehai on 17-6-26.
 */

public class Constants {
    public static final String LOCAL_BOOK_DIRECTORY = Device.currentDevice().getExternalStorageDirectory().getAbsolutePath() + File.separator + Constants.LOCAL_LIBRARY;
    public static final String LOCAL_LIBRARY = "本地书库";
    public static final long MAX_PERCENTAGE = 100;

    public static final String CONFIG_STUDENT_GRADE = "config_student_grade";
    public static final String CONFIG_MATERIAL_PUBLISHER = "config_material_publisher";
    public static final String CONFIG_SCHOOL_LEVEL = "config_school_level";

    public static final String LIBRARY_PARENT_ID = "library_parent_Id";
    public static final String PARENT_ID = "parent_Id";
    public static final String IMPORT_CONTENT_IN_FIRST_BOOT_TAG = "metadata_import_in_first_boot";

    public static final String ACCOUNT_TYPE_HIGH_SCHOOL = "中小学生";
    public static final String ACCOUNT_TYPE_UNIVERSITY = "大学生";
    public static final String ACCOUNT_TYPE_TEACHER = "老师";

    public static final String GRADED_BOOKS = "分级读物";
    public static final String REAL_TIME_BOOKS_LIBRARY_NAME = "时文篇章";
    public static final String SCHOOL_BASED_MATERIALS_LIBRARY_NAME = "校本材料";
    public static final String MY_BOOKS = "我的读物";
    public static final String PROFESSIONAL_MATERIALS = "我的读物";

    public static final int ACCOUNT_TYPE_DICT_FUNCTION = 3;
    public static final int ACCOUNT_TYPE_DICT_LANGUAGE = 5;

    public static final String CHINESE_DICTIONARY = "/dicts/Chinese_dictionary";
    public static final String ENGLISH_DICTIONARY = "/dicts/English_dictionary";
    public static final String JAPANESE_DICTIONARY = "/dicts/Japanese_dictionary";
    public static final String FRENCH_DICTIONARY = "/dicts/french_dictionary";
    public static final String EDITQUERY = "editQuery";
    public static final String LOCATION = "location";
    public static final String DICTTYPE = "dictType";
    public static final String READER_RESPONSE_TYPE = "reader_response_type";
    public static final String NEW_WORD_BEAN = "New_Word_Bean";
    public static final String FRAGMENT_TYPE = "fragment_type";
    public static final String MINUTE_POSITION = "minutePosition";
    public static final String HOUR_POSITION = "hourPosition";
    public static final String SPEECH_TIME = "speechTime";
    public static final String JUMP_SOURCE = "jumpSource";
    public static final String GROUP_ID = "groupId";
    public static final String INFORMAL_ESSAY_CONTENT = "informalEssayContent";
    public static final String INFORMAL_ESSAY_TITLE = "informalEssayTitle";

    public static final int ACCOUNT_TYPE_MY_TRACKS = 0;
    public static final int ACCOUNT_TYPE_MY_THINK = 1;
    public static final int ACCOUNT_TYPE_MY_CREATION = 2;
    public static final int ACCOUNT_HEAR_AND_SPEAK = 4;

    public static final int ENGLISH_TYPE = 0;
    public static final int CHINESE_TYPE = 1;
    public static final int OTHER_TYPE = 2;

    public static final int ENGLISH_TAG = 0;
    public static final int CHINESE_TAG = 1;
    public static final int JAPANESE_TAG = 2;
    public static final int FRENCH_TAG = 3;

    public static final int DIALOG_VIEW_FIRST_TYPE = 0;
    public static final int DIALOG_VIEW_SECOND_TYPE = 1;
    public static final int DIALOG_VIEW_THIRD_TYPE = 2;
    public static final int DIALOG_VIEW_FOURTH_TYPE = 3;

    public static final int READING_RATE_DIALOG_EXPORT = 0;
    public static final int READING_RATE_DIALOG_SHARE = 1;

    public static final int MY_NOTE_TO_INFORMAL_ESSAY = 0;
    public static final int RECORD_TIME_SETTING_TO_INFORMAL_ESSAY = 1;

    public static final int ACCOUNT_TYPE_GOOD_SENTENCE = 0;
    public static final int ACCOUNT_TYPE_NEW_WORD = 1;

    public static final String BAIDU_BAIKE_URL = "https://wapbaike.baidu.com/item/";
    public static final String WIKTIONARY_URL = "https://en.wiktionary.org/wiki/";

    public static final String SCRIBBLE_ACTIVITY_PACKAGE_NAME = "com.onyx.android.note";
    public static final String SCRIBBLE_ACTIVITY_FULL_PATH = "com.onyx.android.note.activity.onyx.ScribbleActivity";
    public static final String STARTUP_ACTIVITY_FULL_PATH = "com.onyx.android.note.activity.StartupActivity";

    public static final String MY_NOTES_FOLDER = "/my_notes_html";
    public static final String NEW_WORD_HTML = MY_NOTES_FOLDER + "/生词本";
    public static final String MEMORANDUM_HTML = MY_NOTES_FOLDER + "/备忘";
    public static final String READER_RESPONSE_HTML = MY_NOTES_FOLDER + "/读后感";
    public static final String INFORMAL_ESSAY_HTML = MY_NOTES_FOLDER + "/随笔";
    public static final String READING_RATE_HTML = MY_NOTES_FOLDER + "/阅读统计";
    public static final String GOOD_SENTENCE_HTML = MY_NOTES_FOLDER + "/好句本";
    public static final String ANNOTATION_HTML = MY_NOTES_FOLDER + "/批注";
    public static final String SUMMARY_HTML = MY_NOTES_FOLDER + "/阅读纪要";
    public static final String UNIT = ".html";

    public static final String ACCOUNT = "account";
    public static final String PASSWORD = "password";

    public static final String APP_DATABASE_DIRECTORY = "/data/data/com.onyx.android.dr/databases/";
    public static final String EMPTY_STRING = "";
    public static final String USER_TYPE = "user_type";
    public static final int HOUR = 24;
    public static final int MINUTE = 60;
    public static final String PREFERRED_BOOKSHELF = "preferred_bookshelf";
    public static final String CHINESE = "中文";
    public static final String ENGLISH = "英文";
    public static final String SMALL_LANGUAGE = "小语种";
    public static final int RECORD_TIME = 5;
    public static final String SEARCH_TYPE = "search_type";
    public static final String NAME_SEARCH = "name";
    public static final String AUTHOR_SEARCH = "author";
    public static final int VALUE_ZERO = 0;
    public static final int VALUE_NEGATIVE_ONE = -1;
    public static final String METADATA_ARRAY = "metadata_array";
    public static final int NEW_WORD_REVIEW_COUNT = 5;
    public static final String GROUP_NAME = "group_name";
    public static final String AUTO_LOGIN_FLAG = "auto_login_flag";
    public static final String BOOKSHELF_TYPE = "bookshelf_type";
    public static final String SEARCH_KEYWORD = "search_keyword";
    public static final String LANGUAGE_BOOKSHELF = "language_bookshelf";
    public static final String GRADED_BOOKSHELF = "graded_bookshelf";
    public static final String ORDER_ID = "order_id";
    public static final String READ_HISTORY_URI = "content://com.onyx.android.sdk.OnyxCmsProvider/library_metadata";
    public static String STANDBY_PIC_DIRECTORY = "/data/local/assets/images/";
    public static final String APK_NAME = "dr.apk";
    public static final String APK_DOWNLOAD_PATH = Device.currentDevice.getExternalStorageDirectory() + File.separator + Constants.APK_NAME;
    public static final long RESET_PRESS_TIMEOUT = 1000;
    public static final int SYSTEM_SETTING_PRESS_COUNT = 6;

    static final public String RK_PREFIX = "rk";
    static final public String IMX6_BASED_CONFIG_NAME = "onyx_imx6";
    static final public String RK3026_BASED_CONFIG_NAME = "onyx_rk3026";
    static final public String DEBUG_CONFIG_NAME = "debug";
    static final public String NON_MANUFACTURE_BASED_CONFIG_NAME = "onyx";
    public static final String UPDATE_URL = "update_url";
    public static final String UPDATE_ZIP = "update.zip";
    public static final String BOOK_REPORT_DATA = "book_report_data";
    public static final String MEMORANDUM_DATA = "memorandum_data";
    public static final String BOOK_NAME = "book_name";
    public static final String BOOK_PAGE = "book_page";
    public static final String BOOK_ID = "book_id";
    public static final String IMPRESSION_ID = "impression_id";
    public static final String CHILDREN_ID = "children_id";
    public static final String SHARE_TYPE = "share_type";
    public static final String DICT_SETTING_DATA = "dict_setting_data";
    public static final String MARK_BOOK_ID = "mark_book_id";
    public static final String MARK_TOP = "mark_top";
    public static final String MARK_LEFT = "mark_left";

    public static final int DEVICE_SETTING_FRAGMENT = 0;
    public static final int SCHOOL_CHILDREN = 0;
    public static final int IDENTITY = 1;

    public static final int DICT_QUERY = 0;
    public static final int DICT_OTHER = 1;

    public static final int ACTION_ONE = 0;
    public static final int ACTION_TWO = 1;
    public static final int FOUR = 4;

    public static final int INFORMAL_ESSAY = 0;
    public static final int READING_RATE = 1;
    public static final int READER_RESPONSE = 2;

    public static final int PENDING_TAG = 0;
    public static final int PASS_TAG = 1;
    public static final int REFUSE_TAG = 2;

    public static final int ANNOTATION_SOURCE_TAG = 0;
    public static final int OTHER_SOURCE_TAG = 1;

    public static final int BOOK_SOURCE = 0;
    public static final int INFORMAL_ESSAY_SOURCE_TAG = 1;
    public static final int READER_RESPONSE_SOURCE_TAG = 2;

    public static long divisor = 1000*60;
    public static int wordNumber = 1200;

    public static final String DOWNLOAD_URL = "downloadUrl";
    public static final String UPDATE_MESSAGE = "message";

    public static final String SUNDAY = "星期日";
    public static final String MONDAY = "星期一";
    public static final String TUESDAY = "星期二";
    public static final String WEDNESDAY = "星期三";
    public static final String THURSDAY = "星期四";
    public static final String FRIDAY = "星期五";
    public static final String SATURDAY = "星期六";

    public static final String  MEMORANDUM_DAY_OF_WEEK = "memorandum_day_of_week";
    public static final String MEMORANDUM_TIME = "memorandum_time";
    public static final String MEMORANDUM_MATTER = "memorandum_matter";
    public static final String MEMORANDUM_CURRENT_TIME = "memorandum_current_time";

    public static final String KREADER_PACKAGE_NAME = "com.onyx.kreader";
    public static final String STATISTICS_ACTIVITY_FULL_PATH = "com.onyx.kreader.ui.statistics.StatisticsActivity";
}
