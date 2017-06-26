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
}
