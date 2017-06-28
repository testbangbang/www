package com.onyx.android.sdk.data.model.common;

/**
 * Created by suicheng on 2017/6/28.
 */

public class DeviceInfoShowConfig {
    public boolean screenPositive = true;
    public int rotationAngle = 90;
    public int orientation;
    public int startX;
    public int startY;

    public boolean isScreenPositive() {
        return screenPositive;
    }
}
