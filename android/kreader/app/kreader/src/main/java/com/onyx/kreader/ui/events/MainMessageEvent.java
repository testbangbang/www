package com.onyx.kreader.ui.events;

import android.graphics.Bitmap;
import com.onyx.android.sdk.common.request.BaseRequest;

/**
 * Created by zhuzeng on 7/28/16.
 */
public class MainMessageEvent {

    private Bitmap bitmap;
    private MessageEventType eventType;

    public MainMessageEvent(MessageEventType eventType){
        this.eventType = eventType;
    }

    public static MainMessageEvent fromRequest(final BaseRequest request, final Throwable throwable) {
        MainMessageEvent mainMessageEvent = new MainMessageEvent(MessageEventType.REQUEST_FINISHED);
        return mainMessageEvent;
    }

    public static MainMessageEvent quitApplication(){
        MainMessageEvent mainMessageEvent = new MainMessageEvent(MessageEventType.QUIT_APPLICATION);
        return mainMessageEvent;
    }

    public final Bitmap getBitmap() {
        return null;
    }

    public MessageEventType getEventType() {
        return eventType;
    }
}
