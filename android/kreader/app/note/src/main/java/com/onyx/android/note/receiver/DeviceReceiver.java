package com.onyx.android.note.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by zhuzeng on 7/11/16.
 */
public class DeviceReceiver extends BroadcastReceiver {

    private SystemUIChangeListener systemUIChangeListener;

    public static final String SYSTEM_UI_DIALOG_OPEN_ACTION = "com.android.systemui.SYSTEM_UI_DIALOG_OPEN_ACTION";
    public static final String SYSTEM_UI_DIALOG_CLOSE_ACTION = "com.android.systemui.SYSTEM_UI_DIALOG_CLOSE_ACTION";


    public static abstract class SystemUIChangeListener {
        public abstract void onSystemUIChanged(final String type, boolean open);
    }

    public void setSystemUIChangeListener(final SystemUIChangeListener listener) {
        systemUIChangeListener = listener;
    }

    public final SystemUIChangeListener getSystemUIChangeListener() {
        return systemUIChangeListener;
    }

    public void registerReceiver(final Context context) {
        context.registerReceiver(this, systemIntentFilter());
    }

    public void unregisterReceiver(final Context context) {
        context.unregisterReceiver(this);
    }

    public IntentFilter systemIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SYSTEM_UI_DIALOG_OPEN_ACTION);
        filter.addAction(SYSTEM_UI_DIALOG_CLOSE_ACTION);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        return filter;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (SYSTEM_UI_DIALOG_OPEN_ACTION.equalsIgnoreCase(action)) {
            notifySystemUIChange(intent, true);
        } else if (SYSTEM_UI_DIALOG_CLOSE_ACTION.equalsIgnoreCase(action)) {
            notifySystemUIChange(intent, false);
        } else if (Intent.ACTION_SCREEN_ON.equalsIgnoreCase(action)) {
            notifySystemUIChange(intent, false);
        }
    }

    private void notifySystemUIChange(final Intent intent, boolean open) {
        if (getSystemUIChangeListener() == null) {
            return;
        }
        getSystemUIChangeListener().onSystemUIChanged(intent.getAction(), open);
    }





}