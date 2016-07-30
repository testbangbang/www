package com.onyx.kreader.ui.events;

import android.graphics.Bitmap;
import com.onyx.android.sdk.common.request.BaseRequest;

/**
 * Created by zhuzeng on 7/28/16.
 */
public class RequestFinishEvent {

    private Bitmap bitmap;

    public static RequestFinishEvent fromRequest(final BaseRequest request, final Throwable throwable) {
        RequestFinishEvent requestFinishEvent = new RequestFinishEvent();
        return requestFinishEvent;
    }

    public final Bitmap getBitmap() {
        return null;
    }

}
