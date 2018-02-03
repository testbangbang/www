package com.onyx.jdread.main.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.onyx.jdread.JDReadApplication;

/**
 * Created by li on 2018/2/2.
 */

public class ScreenStateReceive extends BroadcastReceiver {
    public static final String SCREEN_ON = "android.intent.action.SCREEN_ON";
    public static final String SCREEN_OFF = "android.intent.action.SCREEN_OFF";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (SCREEN_ON.equals(intent.getAction())) {
            JDReadApplication.getInstance().lockScreen();
        }
    }
}
