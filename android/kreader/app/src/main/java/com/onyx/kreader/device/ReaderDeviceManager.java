package com.onyx.kreader.device;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;

/**
 * Created by Joy on 2016/5/6.
 */
public class ReaderDeviceManager {

    private static final String TAG = ReaderDeviceManager.class.getSimpleName();

    private final static String SHOW_STATUS_BAR_ACTION = "show_status_bar";
    private final static String HIDE_STATUS_BAR_ACTION = "hide_status_bar";

    public static void setFullScreen(Context context, boolean fullScreen) {
        Intent intent;
        if (fullScreen) {
            intent = new Intent(HIDE_STATUS_BAR_ACTION);
        } else {
            intent = new Intent(SHOW_STATUS_BAR_ACTION);
        }
        context.sendBroadcast(intent);
    }

    public static void applyGCInvalidate(View view) {
        EpdController.setViewDefaultUpdateMode(view, UpdateMode.GC16);
    }

    public static void enterAnimationUpdate(boolean clear) {
        EpdController.applyApplicationFastMode("reader-device", true, clear);
    }

    public static void exitAnimationUpdate(boolean clear) {
        EpdController.applyApplicationFastMode("reader-device", false, clear);
    }

}
