package com.onyx.edu.homework.data;

import com.onyx.android.sdk.device.EnvironmentUtil;

import java.io.File;

/**
 * Created by lxm on 2017/12/6.
 */

public class Constant {

    public static final String TAG_QUESTION = "question";
    public static final String NOTE_TITLE = "homework";

    public static final int MAX_SUB_NOTE_PAGE_COUNT = 10;

    public static String getRenderPagePath(String documentId, String pageName) {
        return getRenderPageDir()
                + File.separator
                + documentId
                + File.separator
                + pageName
                + ".png";
    }

    public static String getRenderPageDir() {
        return EnvironmentUtil.getExternalStorageDirectory().getPath()
                + File.separator
                + "homework"
                + File.separator
                + "note";
    }
}
