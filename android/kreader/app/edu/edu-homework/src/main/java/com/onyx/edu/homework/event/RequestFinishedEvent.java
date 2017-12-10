package com.onyx.edu.homework.event;

import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by lxm on 2017/12/6.
 */

public class RequestFinishedEvent {

    public BaseNoteRequest request;
    public Throwable throwable;
    public boolean updatePage;

    public RequestFinishedEvent(BaseNoteRequest request, Throwable throwable, boolean updatePage) {
        this.request = request;
        this.throwable = throwable;
        this.updatePage = updatePage;
    }


    public static RequestFinishedEvent create(BaseNoteRequest request, Throwable throwable, boolean updatePage) {
        return new RequestFinishedEvent(request, throwable, updatePage);
    }
}
