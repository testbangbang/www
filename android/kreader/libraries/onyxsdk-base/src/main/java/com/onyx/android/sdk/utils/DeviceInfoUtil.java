package com.onyx.android.sdk.utils;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.onyx.android.sdk.device.Device;

import java.io.File;

/**
 * Created by suicheng on 2017/3/10.
 */
public class DeviceInfoUtil {

    public static File getExternalStorageDirectory() {
        return Device.currentDevice.getExternalStorageDirectory();
    }

    public static File getRemovableSDCardDirectory() {
        return Device.currentDevice.getRemovableSDCardDirectory();
    }

    public static Point getScreenResolution(final Context context) {
        WindowManager w = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);

        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;

        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try {
                widthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
                heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
            } catch (Exception ignored) {
            }
        }
        if (Build.VERSION.SDK_INT >= 17) {
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                widthPixels = realSize.x;
                heightPixels = realSize.y;
            } catch (Exception ignored) {
            }
        }
        return new Point(widthPixels, heightPixels);
    }

    public static String deviceInfo() {
        String s = "";
        s += "\n OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
        s += "\n OS API Level: " + Build.VERSION.SDK_INT;
        s += "\n Device: " + android.os.Build.DEVICE;
        s += "\n Model (and Product): " + android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")";
        return s;
    }

    static public String getPanelVCom() {
        return OnyxSystemProperties.get("sys.panel.vcom", "");
    }

    static public String getPanelNvmVersion() {
        return OnyxSystemProperties.get("sys.panel.nvm_ver", "");
    }

    static public String getPanelPartNo() {
        return OnyxSystemProperties.get("sys.panel.partno", "");
    }

    static public String getPanelWaveFormVersion() {
        return OnyxSystemProperties.get("sys.panel.wf_version", "");
    }

    static public String getBarCode() {
        return OnyxSystemProperties.get("sys.panel.barcode", "");
    }

    static public String getDigitizerFW() {
        return OnyxSystemProperties.get("sys.onyx.emtp", "");
    }
}
