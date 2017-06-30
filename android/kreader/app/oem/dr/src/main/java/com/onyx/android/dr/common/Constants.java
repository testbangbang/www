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
}
