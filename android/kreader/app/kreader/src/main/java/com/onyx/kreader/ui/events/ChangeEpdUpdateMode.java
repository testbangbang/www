package com.onyx.kreader.ui.events;

import com.onyx.android.sdk.api.device.epd.UpdateMode;

/**
 * Created by joy on 8/26/16.
 */
public class ChangeEpdUpdateMode {
    private UpdateMode targetMode;

    public ChangeEpdUpdateMode(final UpdateMode mode) {
        targetMode = mode;
    }

    public UpdateMode getTargetMode() {
        return targetMode;
    }
}
