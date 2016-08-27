package com.onyx.kreader.device;

import android.view.View;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;

/**
 * Created by joy on 8/24/16.
 */
public class EpdDevice {
    public void applyGCUpdate(View view) {
    }

    public void applyRegalUpdate(View view) {
    }

    public void applyRegalClearUpdate(View view) {
    }

    public void setUpdateMode(View view, UpdateMode mode) {
        EpdController.setViewDefaultUpdateMode(view, mode);
    }

    public void resetUpdate(View view) {
    }
}
