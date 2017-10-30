package com.onyx.android.dr.reader.event;

import android.graphics.Bitmap;
import com.onyx.android.sdk.common.request.BaseRequest;

/**
 * Created by zhuzeng on 7/28/16.
 */
public class RequestFinishEvent {

    private boolean applyGCIntervalUpdate = true;
    private boolean renderShapeData = true;
    private boolean waitForShapeData = false;
    private int sequence;

    public static RequestFinishEvent fromRequest(final BaseRequest request, final Throwable throwable, boolean applyGCInterval) {
        RequestFinishEvent requestFinishEvent = new RequestFinishEvent();
        requestFinishEvent.setApplyGCIntervalUpdate(applyGCInterval);
        return requestFinishEvent;
    }

    public static RequestFinishEvent createEvent(int sequence, boolean applyGC, boolean renderShape, boolean waitForShapeData) {
        RequestFinishEvent requestFinishEvent = new RequestFinishEvent();
        requestFinishEvent.setApplyGCIntervalUpdate(applyGC);
        requestFinishEvent.setRenderShapeData(renderShape);
        requestFinishEvent.setWaitForShapeData(waitForShapeData);
        requestFinishEvent.setSequence(sequence);
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

    public boolean isWaitForShapeData() {
        return waitForShapeData;
    }

    public void setWaitForShapeData(boolean waitForShapeData) {
        this.waitForShapeData = waitForShapeData;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
