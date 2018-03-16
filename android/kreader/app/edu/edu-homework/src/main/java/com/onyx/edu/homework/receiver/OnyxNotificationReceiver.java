package com.onyx.edu.homework.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.onyx.edu.homework.data.NotificationType;

/**
 * Created by lxm on 2018/1/16.
 */

public class OnyxNotificationReceiver extends BroadcastReceiver {

    public static final String ONYX_NOTIFICATION_ACTION = "com.onyx.NOTIFICATION_ACTION";

    public static final String ONYX_NOTIFICATION_TYPE = "type";
    public static final String ONYX_NOTIFICATION_DATA = "data";

    public static final String TYPE_NOTIFY_HOMEWORK = "homework";
    public static final String TYPE_NOTIFY_HOMEWORK_READ_ACTIVE = "homework_readActive";
    public static final String TYPE_NOTIFY_HOMEWORK_END_TIME = "homework_endTime";
    public static final String TYPE_NOTIFY_HOMEWORK_CHECKED = "homework_checked";

    public static abstract class OnyxNotificationListener {
        public abstract void onHomeworkNotificationReceive(NotificationType type, String data);
    }

    private OnyxNotificationListener onyxNotificationListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        String type = intent.getStringExtra(ONYX_NOTIFICATION_TYPE);
        String data = intent.getStringExtra(ONYX_NOTIFICATION_DATA);
        NotificationType notifyType = NotificationType.getNotificationType(type);
        if (notifyType.isHomeWorkNotifyType()) {
            onHomeworkMessageReceive(notifyType, data);
        }
    }

    private void onHomeworkMessageReceive(NotificationType type, String data) {
        if (getOnyxNotificationListener() == null) {
            return;
        }
        getOnyxNotificationListener().onHomeworkNotificationReceive(type, data);
    }

    public void setOnyxNotificationListener(OnyxNotificationListener onyxNotificationListener) {
        this.onyxNotificationListener = onyxNotificationListener;
    }

    public OnyxNotificationListener getOnyxNotificationListener() {
        return onyxNotificationListener;
    }

    public void registerReceiver(final Context context) {
        context.registerReceiver(this, onyxIntentFilter());
    }

    public void unregisterReceiver(final Context context) {
        context.unregisterReceiver(this);
    }

    public IntentFilter onyxIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ONYX_NOTIFICATION_ACTION);
        return filter;
    }
}
