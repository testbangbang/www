package com.onyx.kreader.ui.events;

import android.graphics.Bitmap;
import com.onyx.android.sdk.common.request.BaseRequest;

/**
 * Created by zhuzeng on 7/28/16.
 */
public class RequestFinishEvent {

    private boolean applyGCIntervalUpdate = true;

    public static RequestFinishEvent fromRequest(final BaseRequest request, final Throwable throwable, boolean applyGCInterval) {
        RequestFinishEvent requestFinishEvent = new RequestFinishEvent();
        requestFinishEvent.setApplyGCIntervalUpdate(applyGCInterval);
        return requestFinishEvent;
    }

    public void setApplyGCIntervalUpdate(boolean apply) {
        applyGCIntervalUpdate = apply;
    }

    public final boolean isApplyGCIntervalUpdate() {
        return applyGCIntervalUpdate;
    }

}
