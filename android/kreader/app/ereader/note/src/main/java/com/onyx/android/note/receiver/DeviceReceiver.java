package com.onyx.android.note.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

/**
 * Created by zhuzeng on 7/11/16.
 */
public class DeviceReceiver extends BroadcastReceiver {

    private SystemUIChangeListener systemUIChangeListener;

    public static final String SYSTEM_UI_DIALOG_OPEN_ACTION = "com.android.systemui.SYSTEM_UI_DIALOG_OPEN_ACTION";
    public static final String SYSTEM_UI_DIALOG_CLOSE_ACTION = "com.android.systemui.SYSTEM_UI_DIALOG_CLOSE_ACTION";
    public static final String STATUS_BAR_ICON_REFRESH_START_ACTION = "com.android.systemui.STATUS_BAR_ICON_REFRESH_START_ACTION";
    public static final String STATUS_BAR_ICON_REFRESH_FINISH_ACTION = "com.android.systemui.STATUS_BAR_ICON_REFRESH_FINISH_ACTION";

    public static final String SYSTEM_WAKE_UP = "com.android.system.WAKE_UP";
    public static final String SYSTEM_HOME = "com.android.systemui.HOME_BUTTON_CLICK";

    public static final String SYSTEM_UI_SCREEN_SHOT_START_ACTION = "com.android.systemui.SYSTEM_UI_SCREEN_SHOT_START_ACTION";
    public static final String SYSTEM_UI_SCREEN_SHOT_END_ACTION = "com.android.systemui.SYSTEM_UI_SCREEN_SHOT_END_ACTION";

    public static abstract class SystemUIChangeListener {
        public abstract void onSystemUIChanged(final String type, boolean open);
        public abstract void onHomeClicked();

        public abstract void onScreenShot(Intent intent, final boolean end);
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
        filter.addAction(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? Intent.ACTION_SCREEN_ON : SYSTEM_WAKE_UP);
        filter.addAction(SYSTEM_HOME);
        filter.addAction(STATUS_BAR_ICON_REFRESH_START_ACTION);
        filter.addAction(STATUS_BAR_ICON_REFRESH_FINISH_ACTION);
        filter.addAction(SYSTEM_UI_SCREEN_SHOT_START_ACTION);
        filter.addAction(SYSTEM_UI_SCREEN_SHOT_END_ACTION);
        return filter;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e("TAG", "onReceive: "+action);
        switch (action) {
            case SYSTEM_UI_DIALOG_OPEN_ACTION:
            case STATUS_BAR_ICON_REFRESH_START_ACTION:
                notifySystemUIChange(intent, true);
                break;
            case SYSTEM_UI_DIALOG_CLOSE_ACTION:
            case SYSTEM_WAKE_UP:
            case STATUS_BAR_ICON_REFRESH_FINISH_ACTION:
            case Intent.ACTION_SCREEN_ON:
                notifySystemUIChange(intent, false);
                break;
            case SYSTEM_HOME:
                notifyHomeClicked(intent);
                break;
            case SYSTEM_UI_SCREEN_SHOT_START_ACTION:
                notifyScreenShot(intent, false);
                break;
            case SYSTEM_UI_SCREEN_SHOT_END_ACTION:
                notifyScreenShot(intent, true);
                break;
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

    private void notifyScreenShot(final Intent intent,boolean end){
        if (getSystemUIChangeListener() == null) {
            return;
        }
        getSystemUIChangeListener().onScreenShot(intent, end);
    }


}