package com.onyx.android.eschool.utils;

import android.content.Context;
import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by suicheng on 2018/1/8.
 */
public class BroadcastHelper {
    public static final String SYSTEM_UI_DIALOG_OPEN_ACTION = "com.android.systemui.SYSTEM_UI_DIALOG_OPEN_ACTION";
    public static final String SYSTEM_UI_DIALOG_CLOSE_ACTION = "com.android.systemui.SYSTEM_UI_DIALOG_CLOSE_ACTION";
    public static final String DIALOG_TYPE = "dialog_type";

    public static final String NOTIFICATION_ACTION = "com.onyx.NOTIFICATION_ACTION";
    public static final String NOTIFICATION_TYPE = "type";
    public static final String NOTIFICATION_DATA = "data";

    public static final String NOTIFY_MESSAGE_TRIGGER = "notify_message_trigger";
    public final static String STATUS_BAR_MESSAGE_SHOW_ACTION = "status_bar_show_message";

    public static void sendDialogOpenBroadcast(Context context, String dialogType) {
        context.sendBroadcast(intentWith(SYSTEM_UI_DIALOG_OPEN_ACTION, DIALOG_TYPE, dialogType));
    }

    public static void sendDialogCloseBroadcast(Context context, String dialogType) {
        context.sendBroadcast(intentWith(SYSTEM_UI_DIALOG_CLOSE_ACTION, DIALOG_TYPE, dialogType));
    }

    public static void sendStatusBarMessageShowBroadcast(Context context) {
        context.sendBroadcast(new Intent(STATUS_BAR_MESSAGE_SHOW_ACTION));
    }

    public static void sendNotificationBroadcast(Context context, String type, String data) {
        Map<String, String> map = new HashMap<>();
        map.put(NOTIFICATION_TYPE, type);
        map.put(NOTIFICATION_DATA, data);
        context.sendBroadcast(intentWith(NOTIFICATION_ACTION, map));
    }

    private static Intent intentWith(final String action, final String extraName, final String extraValue) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(extraName, extraValue);
        return intent;
    }

    private static Intent intentWith(final String action, final Map<String, String> extraMap) {
        Intent intent = new Intent();
        intent.setAction(action);
        for (Map.Entry<String, String> entry : extraMap.entrySet()) {
            intent.putExtra(entry.getKey(), entry.getValue());
        }
        return intent;
    }
}
