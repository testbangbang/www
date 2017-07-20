package com.onyx.android.dr.reader.common;

import com.onyx.android.sdk.device.EnvironmentUtil;

import java.io.File;

/**
 * Created by huxiaomao on 17/5/25.
 */

public class ReaderConstants {
    public static final String BOOK_PASSWORD = "Password";
    public static final String BOOK_NAME = "bookName";
    public static final String IS_FLUENT = "isFluent";
    public static final String SCREENSHOT_PATH = EnvironmentUtil.getExternalStorageDirectory() + File.separator + "Screenshots/";
    public static float SCREENSHOT_HEIGHT = 120;
    public static final int CREATED_DIRECTORY = 1073742080;
}
