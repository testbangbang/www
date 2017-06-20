package com.onyx.android.sdk.data.model.common;

import java.io.File;

/**
 * Created by suicheng on 2017/6/4.
 */
public class ScreenSaverConfig {
    public String screenSaverName;
    public String targetFormat;
    public String targetDir;
    public int screenSaverInitialNumber;
    public int picRotateDegrees;

    public String sourcePicPathString;
    public String targetPicPathString;

    public int fullScreenPhysicalHeight;
    public int fullScreenPhysicalWidth;

    public boolean convertToGrayScale = true;

    public ScreenSaverConfig(String name, String format, String targetDir, int initialNumber, int targetWidth, int targetHeight) {
        this.screenSaverName = name;
        this.targetFormat = format;
        this.targetDir = targetDir;
        this.screenSaverInitialNumber = initialNumber;
        this.fullScreenPhysicalHeight = targetHeight;
        this.fullScreenPhysicalWidth = targetWidth;
    }

    public String createTargetPicPath(int index) {
        return targetPicPathString = targetDir + File.separator +
                screenSaverName + index + targetFormat;
    }

    public ScreenSaverConfig copy() {
        ScreenSaverConfig config = new ScreenSaverConfig(screenSaverName, targetFormat, targetDir,
                screenSaverInitialNumber, fullScreenPhysicalWidth, fullScreenPhysicalHeight);
        config.sourcePicPathString = sourcePicPathString;
        config.targetPicPathString = targetPicPathString;
        config.convertToGrayScale = convertToGrayScale;
        config.picRotateDegrees = picRotateDegrees;
        return config;
    }
}
