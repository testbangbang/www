package com.onyx.kreader.ui.events;

import android.graphics.Bitmap;
import com.onyx.android.sdk.common.request.BaseRequest;

/**
 * Created by zhuzeng on 7/28/16.
 */
public class RequestFinishEvent {

    private boolean applyGCIntervalUpdate = true;
    private boolean renderShapeData = true;

    public static RequestFinishEvent fromRequest(final BaseRequest request, final Throwable throwable, boolean applyGCInterval) {
        RequestFinishEvent requestFinishEvent = new RequestFinishEvent();
        requestFinishEvent.setApplyGCIntervalUpdate(applyGCInterval);
        return requestFinishEvent;
    }

    public static RequestFinishEvent shapeReadyEvent() {
        return createEvent(false, false);
    }

    public static RequestFinishEvent createEvent(boolean applyGC, boolean renderShape) {
        RequestFinishEvent requestFinishEvent = new RequestFinishEvent();
        requestFinishEvent.setApplyGCIntervalUpdate(applyGC);
        requestFinishEvent.setRenderShapeData(renderShape);
        return requestFinishEvent;
    }

    public void setApplyGCIntervalUpdate(boolean apply) {
        applyGCIntervalUpdate = apply;
    }

    public final boolean isApplyGCIntervalUpdate() {
        return applyGCIntervalUpdate;
    }

    public void setRenderShapeData(boolean renderShapeData) {
        this.renderShapeData = renderShapeData;
    }

    public boolean isRenderShapeData() {
        return renderShapeData;
    }
}
