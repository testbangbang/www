package com.onyx.android.dr.util;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import java.io.File;

/**
 * Created by huxiaomao on 17/7/1.
 */

public class SilentInstall {
    private static final String TAG = SilentInstall.class.getSimpleName();
    public static final String REBOOT = "reboot";

    public static boolean installApk(final Context context,String installerPath) {
        File f = new File(installerPath);
        if (!f.exists()) {
            Log.w(TAG, "file not exist: " + installerPath);
            return false;
        }

        Process process = null;
        int res = 0;
        try {
            process = Runtime.getRuntime().exec("pm install -r " + installerPath);
            res = process.waitFor();
            Log.d(TAG, "install package: " + installerPath + ", " + res);
            rebootDevice(context);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            try {
                if (process != null) {
                    process.exitValue();
                }
            } catch (IllegalThreadStateException e) {
                process.destroy();
            }

        }
        return false;
    }

    public static void rebootDevice(final Context context) {
        try {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            powerManager.reboot(REBOOT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
