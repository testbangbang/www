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

    public static final String ACCOUNT_TYPE_HIGH_SCHOOL = "account_type_high_school";
    public static final String ACCOUNT_TYPE_UNIVERSITY = "account_type_university";
    public static final String ACCOUNT_TYPE_TEACHER = "account_type_teacher";

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
    public static final String EDITQUERY = "editQuery";
    public static final String DICTTYPE = "dictType";
    public static final String MINUTE_POSITION = "minutePosition";
    public static final String HOUR_POSITION = "hourPosition";

    public static final int ACCOUNT_TYPE_MY_TRACKS = 0;
    public static final int ACCOUNT_TYPE_MY_THINK = 1;
    public static final int ACCOUNT_TYPE_MY_CREATION = 2;
    public static final int ACCOUNT_HEAR_AND_SPEAK = 4;

    public static final int ENGLISH_NEW_WORD_NOTEBOOK = 0;
    public static final int CHINESE_NEW_WORD_NOTEBOOK = 1;
    public static final int JAPANESE_NEW_WORD_NOTEBOOK = 2;

    public static final int DIALOG_VIEW_FIRST_TYPE = 0;
    public static final int DIALOG_VIEW_SECOND_TYPE = 1;
    public static final int DIALOG_VIEW_THIRD_TYPE = 2;

    public static final int ACCOUNT_TYPE_GOOD_SENTENCE = 0;
    public static final int ACCOUNT_TYPE_NEW_WORD = 1;

    public static final String BAIDU_BAIKE_URL = "https://wapbaike.baidu.com/item/";
    public static final String WIKTIONARY_URL = "https://en.wiktionary.org/wiki/";

    public static final String SCRIBBLE_ACTIVITY_PACKAGE_NAME = "com.onyx.android.note";
    public static final String SCRIBBLE_ACTIVITY_FULL_PATH = "com.onyx.android.note.activity.onyx.ScribbleActivity";
    public static final String STARTUP_ACTIVITY_FULL_PATH = "com.onyx.android.note.activity.StartupActivity";

    public static final String MY_NOTES_FOLDER = "/my_notes_html";
    public static final String NEW_WORD_HTML = MY_NOTES_FOLDER + "/new_word.html";
    public static final String INFORMAL_ESSAY_HTML = MY_NOTES_FOLDER + "/informal_essay.html";
    public static final String GOOD_SENTENCE_HTML = MY_NOTES_FOLDER + "/good_sentence.html";

    public static final int HOUR = 24;
    public static final int MINUTE = 60;
}
