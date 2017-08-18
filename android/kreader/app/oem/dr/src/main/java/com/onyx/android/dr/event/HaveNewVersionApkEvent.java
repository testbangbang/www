package com.onyx.android.dr.event;

import com.onyx.android.sdk.data.model.ApplicationUpdate;

/**
 * Created by hehai on 17-5-27.
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
