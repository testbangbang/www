package com.onyx.edu.homework.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by lxm on 2018/1/16.
 */

public class OnyxMessageReceiver extends BroadcastReceiver {

    public static final String ONYX_NOTIFICATION_ACTION = "com.onyx.NOTIFICATION_ACTION";

    public static final String ONYX_NOTIFICATION_TYPE = "type";
    public static final String ONYX_NOTIFICATION_DATA = "data";

    public static final String ONYX_NOTIFICATION_TYPE_HOMEWORK = "homework";

    public static abstract class OnyxMessageListener {
        public abstract void onHomeworkMessageReceive(String data);
    }

    private OnyxMessageListener onyxMessageListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        String type = intent.getStringExtra(ONYX_NOTIFICATION_TYPE);
        String data = intent.getStringExtra(ONYX_NOTIFICATION_DATA);
        if (type.equals(ONYX_NOTIFICATION_TYPE_HOMEWORK)) {
            onHomeworkMessageReceive(data);
        }
    }

    private void onHomeworkMessageReceive(String data) {
        if (getOnyxMessageListener() == null) {
            return;
        }
        getOnyxMessageListener().onHomeworkMessageReceive(data);
    }

    public void setOnyxMessageListener(OnyxMessageListener onyxMessageListener) {
        this.onyxMessageListener = onyxMessageListener;
    }

    public OnyxMessageListener getOnyxMessageListener() {
        return onyxMessageListener;
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
