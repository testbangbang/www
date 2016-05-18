package com.onyx.android.sdk.device;

public class Platforms {

    public static final String RK2906 = "RK2906";
    public static final String RK3026 = "RK3026";
    public static final String RK2818 = "RK2818";
    public static final String IMX6 = "IMX6";
    public static final String IMX508 = "IMX508";
    public static final String F430 = "F430";

    public static boolean isRK3026() {
        return RK3026.equals(DeviceInfo.currentDevice.getPlatform());
    }

    public static boolean isRK2906() {
        return RK2906.equals(DeviceInfo.currentDevice.getPlatform());
    }

    public static boolean isIMX6() {
        return IMX6.equals(DeviceInfo.currentDevice.getPlatform());
    }

}
