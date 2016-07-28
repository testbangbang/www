package com.onyx.kreader.device;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
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
