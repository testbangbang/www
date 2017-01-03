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
import com.onyx.kreader.ui.data.SingletonSharedPreference;
import com.onyx.kreader.utils.DeviceUtils;

/**
 * Created by Joy on 2016/5/6.
 */
public class ReaderDeviceManager {

    private static final String TAG = ReaderDeviceManager.class.getSimpleName();
    private static final String APP = ReaderDeviceManager.class.getSimpleName();

    private static int gcInterval;
    private static int refreshCount;
    private static boolean inFastUpdateMode = false;

    private final static EpdDevice epdDevice;

    static {
        if (DeviceUtils.isRkDevice()) {
            epdDevice = new EpdRk3026();
        } else {
            epdDevice = new EpdImx6();
        }
    }

    public static void enterAnimationUpdate(boolean clear) {
        if (!inFastUpdateMode) {
            EpdController.applyApplicationFastMode(APP, true, clear);
            inFastUpdateMode = true;
        }
    }

    public static void exitAnimationUpdate(boolean clear) {
        if (inFastUpdateMode) {
            EpdController.applyApplicationFastMode(APP, false, clear);
            inFastUpdateMode = false;
        }
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

    public static int getGcInterval() {
        return gcInterval;
    }

    public static void setGcInterval(int interval) {
        gcInterval = interval - 1;
        refreshCount = 0;
    }

    public static void applyWithGCInterval(View view, boolean isTextPage) {
        if (isUsingRegal(view.getContext())) {
            applyWithGCIntervalWitRegal(view, isTextPage);
        } else {
            applyWithGCIntervalWithoutRegal(view);
        }
    }

    public static boolean isUsingRegal(Context context) {
        boolean useRegal = SingletonSharedPreference.isEnableRegal(context);
        return EpdController.supportRegal() && useRegal;
    }

    public static void enableScreenUpdate(View view, boolean enable) {
        epdDevice.enableScreenUpdate(view, enable);
    }

    public static void refreshScreenWithGCInterval(View view, boolean isTextPage) {
        enableScreenUpdate(view, true);
        if (isTextPage && EpdController.supportRegal()) {
            refreshScreenWithGCIntervalWithRegal(view);
        } else {
            refreshScreenWithGCIntervalWithoutRegal(view);
        }
    }

    public static void refreshScreenWithGCIntervalWithRegal(View view) {
        if (refreshCount++ >= gcInterval) {
            refreshCount = 0;
            epdDevice.refreshScreen(view, UpdateMode.GC);
        } else {
            epdDevice.refreshScreen(view, UpdateMode.REGAL);
        }
    }

    public static void refreshScreenWithGCIntervalWithoutRegal(View view) {
        if (refreshCount++ >= gcInterval) {
            refreshCount = 0;
            epdDevice.refreshScreen(view, UpdateMode.GC);
        } else {
            epdDevice.refreshScreen(view, UpdateMode.GU);
        }
    }

    public static void applyWithGCIntervalWitRegal(View view, boolean textOnly) {
        if (refreshCount++ >= gcInterval) {
            refreshCount = 0;
            epdDevice.applyGCUpdate(view);
        } else {
            epdDevice.applyRegalUpdate(view);
        }
    }

    public static void applyWithGCIntervalWithoutRegal(View view) {
        if (refreshCount++ >= gcInterval) {
            refreshCount = 0;
            epdDevice.applyGCUpdate(view);
        } else {
            epdDevice.resetUpdate(view);
        }
    }

    public static void setUpdateMode(final View view, UpdateMode mode) {
        epdDevice.setUpdateMode(view, mode);
    }

    public static void resetUpdateMode(final View view) {
        epdDevice.resetUpdate(view);
    }

}
