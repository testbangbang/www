package com.onyx.kreader.note.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by zhuzeng on 7/11/16.
 */
public class DeviceReceiver extends BroadcastReceiver {

    private boolean registered = false;
    private SystemUIChangeListener systemUIChangeListener;

    public static final String SYSTEM_UI_DIALOG_OPEN_ACTION = "com.android.systemui.SYSTEM_UI_DIALOG_OPEN_ACTION";
    public static final String SYSTEM_UI_DIALOG_CLOSE_ACTION = "com.android.systemui.SYSTEM_UI_DIALOG_CLOSE_ACTION";
    public static final String STATUS_BAR_ICON_REFRESH_START_ACTION = "com.android.systemui.STATUS_BAR_ICON_REFRESH_START_ACTION";
    public static final String STATUS_BAR_ICON_REFRESH_FINISH_ACTION = "com.android.systemui.STATUS_BAR_ICON_REFRESH_FINISH_ACTION";

    public static final String SYSTEM_WAKE_UP = "com.android.system.WAKE_UP";
    public static final String SYSTEM_HOME = "com.android.systemui.HOME_BUTTON_CLICK";

    public static abstract class SystemUIChangeListener {
        public abstract void onSystemUIChanged(final String type, boolean open);
        public abstract void onHomeClicked();
    }

    public void setSystemUIChangeListener(final SystemUIChangeListener listener) {
        systemUIChangeListener = listener;
    }

    public final SystemUIChangeListener getSystemUIChangeListener() {
        return systemUIChangeListener;
    }

    public void registerReceiver(final Context context) {
        if (!registered && context != null) {
            context.registerReceiver(this, systemIntentFilter());
        }
        registered = true;
    }

    public void unregisterReceiver(final Context context) {
        if (registered && context != null) {
            context.unregisterReceiver(this);
        }
        registered = false;
    }

    public IntentFilter systemIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SYSTEM_UI_DIALOG_OPEN_ACTION);
        filter.addAction(SYSTEM_UI_DIALOG_CLOSE_ACTION);
        filter.addAction(SYSTEM_WAKE_UP);
        filter.addAction(SYSTEM_HOME);
        filter.addAction(STATUS_BAR_ICON_REFRESH_START_ACTION);
        filter.addAction(STATUS_BAR_ICON_REFRESH_FINISH_ACTION);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        return filter;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (SYSTEM_UI_DIALOG_OPEN_ACTION.equalsIgnoreCase(action)) {
            notifySystemUIChange(intent, true);
        } else if (SYSTEM_UI_DIALOG_CLOSE_ACTION.equalsIgnoreCase(action)) {
            notifySystemUIChange(intent, false);
        } else if (SYSTEM_WAKE_UP.equalsIgnoreCase(action)) {
            notifySystemUIChange(intent, false);
        } else if (SYSTEM_HOME.equalsIgnoreCase(action)) {
            notifyHomeClicked(intent);
        } else if (STATUS_BAR_ICON_REFRESH_START_ACTION.equalsIgnoreCase(action)) {
            notifySystemUIChange(intent, true);
        } else if (STATUS_BAR_ICON_REFRESH_FINISH_ACTION.equalsIgnoreCase(action)) {
            notifySystemUIChange(intent, false);
        } else if (Intent.ACTION_SCREEN_ON.equalsIgnoreCase(action)) {
            notifySystemUIChange(intent, false);
        } else if (Intent.ACTION_SCREEN_OFF.equalsIgnoreCase(action)) {
            notifySystemUIChange(intent, true);
        }
    }

    private void notifySystemUIChange(final Intent intent, boolean open) {
        if (getSystemUIChangeListener() == null) {
            return;
        }
        getSystemUIChangeListener().onSystemUIChanged(intent.getAction(), open);
    }

    private void notifyHomeClicked(final Intent intent) {
        if (getSystemUIChangeListener() == null) {
            return;
        }
        getSystemUIChangeListener().onHomeClicked();
    }


}