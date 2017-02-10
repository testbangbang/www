package com.onyx.kreader.device;

import android.view.View;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;

/**
 * Created by joy on 8/24/16.
 */
public class EpdRk3026 extends EpdDevice {

    public void applyGCUpdate(View view) {
        EpdController.setViewDefaultUpdateMode(view, UpdateMode.GC);
        view.invalidate();
    }

    @Override
    public void setUpdateMode(View view, UpdateMode mode) {
        EpdController.setViewDefaultUpdateMode(view, mode);
    }

    public void applyRegalUpdate(View view) {
        EpdController.setViewDefaultUpdateMode(view, UpdateMode.REGAL);
    }

    public void applyRegalClearUpdate(View view) {
        EpdController.setViewDefaultUpdateMode(view, UpdateMode.REGAL);
    }

    public void resetUpdate(View view) {
        EpdController.setViewDefaultUpdateMode(view, UpdateMode.GU);
    }

    public void holdDisplay(boolean hold) {
        EpdController.holdDisplay(hold);
    }
}
