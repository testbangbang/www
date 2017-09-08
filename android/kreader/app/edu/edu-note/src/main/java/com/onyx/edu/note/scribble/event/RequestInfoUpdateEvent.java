package com.onyx.edu.note.scribble.event;

import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;

/**
 * Created by solskjaer49 on 2017/7/20 11:04.
 */

public class RequestInfoUpdateEvent {
    public RequestInfoUpdateEvent(BaseRequest request, Throwable throwable) {
        mRequest = (AsyncBaseNoteRequest) request;
        mThrowable = throwable;
    }

    public AsyncBaseNoteRequest getRequest() {
        return mRequest;
    }

    public Throwable getThrowable() {
        return mThrowable;
    }

    private AsyncBaseNoteRequest mRequest;
    private Throwable mThrowable;
}
