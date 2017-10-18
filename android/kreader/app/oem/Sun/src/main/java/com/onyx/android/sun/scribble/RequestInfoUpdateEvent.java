package com.onyx.android.sun.scribble;

import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.request.ShapeDataInfo;

/**
 * Created by solskjaer49 on 2017/7/20 11:04.
 */

public class RequestInfoUpdateEvent {
    public RequestInfoUpdateEvent(ShapeDataInfo shapeDataInfo, BaseRequest request, Throwable throwable) {
        mRequest = (AsyncBaseNoteRequest) request;
        mThrowable = throwable;
        this.shapeDataInfo = shapeDataInfo;
    }

    public AsyncBaseNoteRequest getRequest() {
        return mRequest;
    }

    public Throwable getThrowable() {
        return mThrowable;
    }

    public ShapeDataInfo getShapeDataInfo() {
        return shapeDataInfo;
    }

    private AsyncBaseNoteRequest mRequest;
    private Throwable mThrowable;
    private ShapeDataInfo shapeDataInfo;
}
