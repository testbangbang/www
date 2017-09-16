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
    public static final String OTHER_DICTIONARY = "/dicts/other_dictionary";
    public static final String EDITQUERY = "editQuery";
    public static final String LOCATION = "location";
    public static final String DICTTYPE = "dictType";
    public static final String NEW_WORD_BEAN = "New_Word_Bean";
    public static final String FRAGMENT_TYPE = "fragment_type";
    public static final String MINUTE_POSITION = "minutePosition";
    public static final String HOUR_POSITION = "hourPosition";
    public static final String SPEECH_TIME = "speechTime";
    public static final String JUMP_SOURCE = "jumpSource";
    public static final String INFORMAL_ESSAY_CONTENT = "informalEssayContent";
    public static final String INFORMAL_ESSAY_TITLE = "informalEssayTitle";

    public static final int ACCOUNT_TYPE_MY_TRACKS = 0;
    public static final int ACCOUNT_TYPE_MY_THINK = 1;
    public static final int ACCOUNT_TYPE_MY_CREATION = 2;
    public static final int ACCOUNT_HEAR_AND_SPEAK = 4;

    public static final int ENGLISH_TYPE = 0;
    public static final int CHINESE_TYPE = 1;
    public static final int OTHER_TYPE = 2;

    public static final int DIALOG_VIEW_FIRST_TYPE = 0;
    public static final int DIALOG_VIEW_SECOND_TYPE = 1;
    public static final int DIALOG_VIEW_THIRD_TYPE = 2;

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
    public static final String NEW_WORD_HTML = MY_NOTES_FOLDER + "/new_word.html";
    public static final String MEMORANDUM_HTML = MY_NOTES_FOLDER + "/memorandum.html";
    public static final String INFORMAL_ESSAY_HTML = MY_NOTES_FOLDER + "/informal_essay.html";
    public static final String GOOD_SENTENCE_HTML = MY_NOTES_FOLDER + "/good_sentence.html";

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

    public static final int DEVICE_SETTING_FRAGMENT = 0;
    public static final int SCHOOL_CHILDREN = 0;
    public static final int IDENTITY = 1;

    public static final int DICT_QUERY = 0;
    public static final int DICT_OTHER = 1;

    public static final int ACTION_ONE = 0;
    public static final int ACTION_TWO = 1;
    public static final int FOUR = 4;

    public static final String PRIMARY_AND_SECONDARY_SCHOOL_STUDENTS = "中小学生";
    public static final String UNDERGRADUATE = "大学生";
    public static final String TEACHER = "老师";
    public static final String SOCIAL_MAN = "社会人";
}
