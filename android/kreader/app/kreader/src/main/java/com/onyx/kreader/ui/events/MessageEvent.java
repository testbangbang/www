package com.onyx.kreader.ui.events;

import android.graphics.Bitmap;
import com.onyx.android.sdk.common.request.BaseRequest;

/**
 * Created by zhuzeng on 7/28/16.
 */
public class MessageEvent {

    private Bitmap bitmap;

    public static MessageEvent fromRequest(final BaseRequest request, final Throwable throwable) {
        MessageEvent messageEvent = new MessageEvent();
        return messageEvent;
    }

    public final Bitmap getBitmap() {
        return null;
    }

}
