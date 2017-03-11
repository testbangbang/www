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
}
