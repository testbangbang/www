package com.onyx.kreader.device;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Joy on 2016/5/6.
 */
public class DeviceController {

    private final String SHOW_STATUS_BAR_ACTION = "show_status_bar";
    private final static String HIDE_STATUS_BAR_ACTION = "hide_status_bar";

    private Context context;

    private DeviceController(Context context) {
        this.context = context;
    }

    public static DeviceController create(Context context) {
        return new DeviceController(context);
    }

    public boolean isFullScreen() {
        throw new IllegalAccessError();
    }

    public void setFullScreen(boolean fullScreen) {
        Intent intent;
        if (fullScreen) {
            intent = new Intent(HIDE_STATUS_BAR_ACTION);
        } else {
            intent = new Intent(SHOW_STATUS_BAR_ACTION);
        }
        context.sendBroadcast(intent);
    }
}
