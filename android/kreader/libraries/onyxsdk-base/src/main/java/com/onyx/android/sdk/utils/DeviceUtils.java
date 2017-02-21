package com.onyx.android.sdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by ming on 2016/12/3.
 */

public class DeviceUtils {

    private final static String SHOW_STATUS_BAR_ACTION = "show_status_bar";
    private final static String HIDE_STATUS_BAR_ACTION = "hide_status_bar";

    public static void setFullScreenOnResume(Activity activity, boolean fullScreen) {
        if (Build.VERSION.SDK_INT >= 19) {
            adjustFullScreenStatusForAPIAbove19(activity,fullScreen);
            return;
        }

        Intent intent;
        if (fullScreen) {
            intent = new Intent(HIDE_STATUS_BAR_ACTION);
        } else {
            intent = new Intent(SHOW_STATUS_BAR_ACTION);
        }
        activity.sendBroadcast(intent);
    }

    static void adjustFullScreenStatusForAPIAbove19(final Activity activity, boolean fullScreen) {
        int clearFlag, targetFlag, uiOption;
        if (fullScreen) {
            clearFlag = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
            targetFlag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
            uiOption = View.SYSTEM_UI_FLAG_FULLSCREEN;
        } else {
            clearFlag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
            targetFlag = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
            uiOption = View.SYSTEM_UI_FLAG_VISIBLE;
        }
        activity.getWindow().clearFlags(clearFlag);
        activity.getWindow().setFlags(targetFlag, targetFlag);
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOption);
    }

    public static void setFullScreenOnCreate(final Activity activity, boolean fullScreen) {
        if (fullScreen) {
            activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public static void exit() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    public static String getMacAddress(Context mContext) {
        String macStr = "";
        WifiManager wifiManager = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            macStr = wifiInfo.getMacAddress();
        }
        return macStr;
    }

    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
                return networkInfo.isAvailable();
        }
        return false;
    }

    public static boolean isFullScreen(Activity activity) {
        int flag = activity.getWindow().getAttributes().flags;
        return (flag & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }
}
