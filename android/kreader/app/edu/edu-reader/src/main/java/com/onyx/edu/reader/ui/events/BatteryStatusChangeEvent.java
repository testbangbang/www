package com.onyx.edu.reader.ui.events;

import android.content.Context;

/**
 * Created by ming on 2017/5/19.
 */

public class BatteryStatusChangeEvent {

    private Context context;
    private int status;
    private int level;

    public BatteryStatusChangeEvent(Context context, int status, int level) {
        this.context = context;
        this.status = status;
        this.level = level;
    }

    public Context getContext() {
        return context;
    }

    public int getStatus() {
        return status;
    }

    public int getLevel() {
        return level;
    }
}
