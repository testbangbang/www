package com.onyx.android.dr.reader.device;

import android.content.Context;
import android.view.View;

import com.onyx.android.dr.reader.data.SingletonSharedPreference;
import com.onyx.android.sdk.api.device.EpdDevice;
import com.onyx.android.sdk.api.device.EpdImx6;
import com.onyx.android.sdk.api.device.EpdRk3026;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.utils.DeviceUtils;

import static com.onyx.android.sdk.api.device.epd.EpdController.enableRegal;

/**
 * Created by Joy on 2016/5/6.
 */
public class ReaderDeviceManager {

    private static final String TAG = ReaderDeviceManager.class.getSimpleName();
    private static final String APP = ReaderDeviceManager.class.getSimpleName();

    private static int gcInterval;
    private static int refreshCount;
    private static boolean inFastUpdateMode = false;
    private static boolean enableHoldDisplay = true;

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

    public static void toggleAnimationUpdate(boolean clear) {
        EpdController.applyApplicationFastMode(APP, !inFastUpdateMode, clear);
        inFastUpdateMode = !inFastUpdateMode;
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

    public static void applyRegalUpdate(final Context context, final View view) {
        if (!isUsingRegal(context)) {
            return;
        }
        epdDevice.setUpdateMode(view, UpdateMode.REGAL);
    }

    public static boolean isUsingRegal(Context context) {
        boolean useRegal = SingletonSharedPreference.isEnableRegal(context);
        return supportRegal(context) && useRegal;
    }

    public static boolean supportRegal(final Context context) {
        boolean regalEnable = DeviceConfig.sharedInstance(context).isRegalEnable();
        return EpdController.supportRegal() && regalEnable;
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

    public static boolean isApplyFullUpdate() {
        return refreshCount == 0;
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
            enableRegal();
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

    public static void applyWithGcUpdate(View view) {
        epdDevice.applyGCUpdate(view);
    }

    public static void setUpdateMode(final View view, UpdateMode mode) {
        epdDevice.setUpdateMode(view, mode);
    }

    public static void resetUpdateMode(final View view) {
        epdDevice.resetUpdate(view);
    }

    public static void cleanUpdateMode(final View view) {
        epdDevice.cleanUpdate(view);
    }
}
