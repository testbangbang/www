package com.onyx.android.dr.event;

import com.onyx.android.sdk.data.request.cloud.FirmwareUpdateRequest;

/**
 * Created by hehai on 17-3-31.
 */
public class HaveNewVersionEvent {
    private FirmwareUpdateRequest request;

    public HaveNewVersionEvent(FirmwareUpdateRequest req) {
        this.request = req;
    }

    public FirmwareUpdateRequest getRequest() {
        return request;
    }
}
