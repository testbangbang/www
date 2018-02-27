package com.onyx.jdread.reader.epd;

import android.content.Context;
import android.view.View;

import com.onyx.android.sdk.api.device.EpdDevice;
import com.onyx.android.sdk.api.device.EpdImx6;
import com.onyx.android.sdk.api.device.EpdRk3026;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.device.IMX6Device;
import com.onyx.android.sdk.device.IMX7Device;
import com.onyx.android.sdk.device.RK3026Device;

/**
 * Created by john on 29/1/2018.
 */

public class ReaderEpdHelper {

    private static final String TAG = ReaderEpdHelper.class.getSimpleName();
    public static final int DEFAULT_GC_INTERVAL = 10;

    private int gcInterval;
    private int refreshCount;
    private boolean inFastUpdateMode = false;
    private boolean useDefaultUpdate = false;

    public ReaderEpdHelper(final Context context) {
        prepareInitialUpdate(context);
    }

    public void enterFastUpdateMode(boolean clear) {
        if (!inFastUpdateMode) {
            EpdController.applyApplicationFastMode(TAG, true, clear);
            inFastUpdateMode = true;
        }
    }

    public void exitFastUpdateMode(boolean clear) {
        if (inFastUpdateMode) {
            EpdController.applyApplicationFastMode(TAG, false, clear);
            inFastUpdateMode = false;
        }
    }

    public void prepareInitialUpdate(final Context context) {
        // read init value from context.
        gcInterval = DEFAULT_GC_INTERVAL;
        refreshCount = gcInterval;
    }

    public boolean isUseDefaultUpdate() {
        return useDefaultUpdate;
    }

    public void setUseDefaultUpdate(boolean useDefaultUpdate) {
        this.useDefaultUpdate = useDefaultUpdate;
    }

    public int getGcInterval() {
        return gcInterval;
    }

    public void setGcInterval(int interval) {
        gcInterval = interval - 1;
        refreshCount = 0;
    }

    public void applyWithGCInterval(View view) {
        if (isUseDefaultUpdate()) {
            resetUpdateMode(view);
        } else {
            applyWithGCIntervalWitRegal(view);
        }
    }

    public void applyWithGCIntervalWitRegal(View view) {
        if (refreshCount++ >= gcInterval) {
            refreshCount = 0;
            applyGCUpdate(view);
        } else {
            applyRegalUpdate(view);
        }
    }

    public static void applyGCUpdate(View view) {
        EpdController.setViewDefaultUpdateMode(view, UpdateMode.GC);
    }

    public static void applyRegalUpdate(View view) {
        EpdController.setViewDefaultUpdateMode(view, UpdateMode.REGAL);
    }

    public static void resetUpdateMode(final View view) {
        EpdController.resetUpdateMode(view);
    }

    public static void cleanUpdateMode(final View view) {

    }
}
