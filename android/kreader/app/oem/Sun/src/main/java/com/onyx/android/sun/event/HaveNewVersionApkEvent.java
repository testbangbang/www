package com.onyx.android.sun.event;

import com.onyx.android.sdk.data.model.ApplicationUpdate;

/**
 * Created by hehai on 17-10-19.
 */

public class HaveNewVersionApkEvent {
    private ApplicationUpdate applicationUpdate;

    public HaveNewVersionApkEvent(ApplicationUpdate applicationUpdate) {
        this.applicationUpdate = applicationUpdate;
    }

    public ApplicationUpdate getApplicationUpdate() {
        return applicationUpdate;
    }
}
