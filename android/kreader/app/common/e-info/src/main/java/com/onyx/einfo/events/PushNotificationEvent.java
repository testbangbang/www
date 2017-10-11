package com.onyx.einfo.events;

import com.onyx.android.sdk.data.model.v2.PushNotification;

/**
 * Created by suicheng on 2017/9/14.
 */

public class PushNotificationEvent {
    public PushNotification notification;

    public PushNotificationEvent(PushNotification notification) {
        this.notification = notification;
    }
}
