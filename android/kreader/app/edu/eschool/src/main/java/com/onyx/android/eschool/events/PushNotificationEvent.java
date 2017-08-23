package com.onyx.android.eschool.events;

import com.onyx.android.sdk.data.model.v2.PushNotification;

/**
 * Created by suicheng on 2017/8/20.
 */

public class PushNotificationEvent {
    public PushNotification notification;

    public PushNotificationEvent(PushNotification notification) {
        this.notification = notification;
    }
}
