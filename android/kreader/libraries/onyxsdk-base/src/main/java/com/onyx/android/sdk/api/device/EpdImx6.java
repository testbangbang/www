package com.onyx.android.sdk.api.device;

import android.view.View;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;

/**
 * Created by joy on 8/24/16.
 */
public class EpdImx6 extends EpdDevice {

    private void useFastScheme() {
        EpdController.setDisplayScheme(EpdController.SCHEME_SCRIBBLE);
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
}
