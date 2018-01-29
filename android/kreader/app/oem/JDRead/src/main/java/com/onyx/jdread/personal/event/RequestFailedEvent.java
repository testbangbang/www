package com.onyx.jdread.personal.event;

import com.onyx.jdread.personal.model.PersonalDataBundle;

/**
 * Created by li on 2018/1/29.
 */

public class RequestFailedEvent {
    private static RequestFailedEvent event;
    private String message;

    public static void sendFailedMessage(String message) {
        if (event == null) {
            event = new RequestFailedEvent();
        }
        event.setMessage(message);
        PersonalDataBundle.getInstance().getEventBus().post(event);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
