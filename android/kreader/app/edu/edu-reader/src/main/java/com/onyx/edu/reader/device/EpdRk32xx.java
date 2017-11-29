package com.onyx.edu.reader.device;

import android.view.View;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;

/**
 * Created by john on 5/10/2017.
 */

public class EpdRk32xx extends EpdDevice {

    private void useFastScheme() {
    }

    public void applyGCUpdate(View view) {
        EpdController.setViewDefaultUpdateMode(view, UpdateMode.GC);
    }

    public void applyRegalUpdate(View view) {
        EpdController.setViewDefaultUpdateMode(view, UpdateMode.REGAL);
    }

    public void applyRegalClearUpdate(View view) {
        EpdController.setViewDefaultUpdateMode(view, UpdateMode.REGAL_D);
    }

    @Override
    public void setUpdateMode(View view, UpdateMode mode) {
        EpdController.setViewDefaultUpdateMode(view, mode);
        useFastScheme();
    }

    public void resetUpdate(View view) {
        EpdController.resetUpdateMode(view);
        useFastScheme();
    }

    @Override
    public void cleanUpdate(View view) {
        resetUpdate(view);
    }

    @Override
    public void enableRegal() {
        EpdController.enableRegal();
    }

    @Override
    public void disableRegal() {
        EpdController.disableRegal();
    }
}

