package com.onyx.kreader.device;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;

/**
 * Created by Joy on 2016/5/6.
 */
public class ReaderDeviceManager {

    private static final String TAG = ReaderDeviceManager.class.getSimpleName();
    private static final String APP = ReaderDeviceManager.class.getSimpleName();

    private final static String SHOW_STATUS_BAR_ACTION = "show_status_bar";
    private final static String HIDE_STATUS_BAR_ACTION = "hide_status_bar";

    private static int gcInterval;
    private static int refreshCount;

    public static int getScreenOrientation(final Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch(rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    Log.e(TAG, "Unknown screen orientation. Defaulting to " +
                            "portrait.");
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch(rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    Log.e(TAG, "Unknown screen orientation. Defaulting to " +
                            "landscape.");
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }

    public static void setFullScreen(Activity activity, boolean fullScreen) {
        if (Build.VERSION.SDK_INT >= 19) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
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

    public static void applyGCUpdate(View view) {
        EpdController.setViewDefaultUpdateMode(view, UpdateMode.GC);
    }

    public static void enterAnimationUpdate(boolean clear) {
        EpdController.applyApplicationFastMode(APP, true, clear);
    }

    public static void exitAnimationUpdate(boolean clear) {
        EpdController.applyApplicationFastMode(APP, false, clear);
    }

    public static void startScreenHandWriting(final View view) {
        EpdController.setScreenHandWritingPenState(view, 1);
    }

    public static void stopScreenHandWriting(final View view) {
        EpdController.setScreenHandWritingPenState(view, 0);
    }

    public static void prepareInitialUpdate(int interval) {
        gcInterval = interval - 1;
        refreshCount = gcInterval;
    }

    public static void setGcInterval(int interval) {
        gcInterval = interval - 1;
        refreshCount = 0;
    }

    public static void applyWithGCInterval(View view) {
        if (refreshCount++ >= gcInterval) {
            refreshCount = 0;
            applyGCUpdate(view);
        } else {
            resetUpdate(view);
        }
    }

    public static void resetUpdate(View view) {
        EpdController.resetUpdateMode(view);
    }

}
