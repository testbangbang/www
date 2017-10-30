package com.onyx.android.dr.reader.event;

import com.onyx.android.sdk.api.device.epd.UpdateMode;

/**
 * Created by joy on 8/26/16.
 */
public class ChangeEpdUpdateModeEvent {
    private UpdateMode targetMode;

    public ChangeEpdUpdateModeEvent(final UpdateMode mode) {
        targetMode = mode;
    }

    public UpdateMode getTargetMode() {
        return targetMode;
    }
}
