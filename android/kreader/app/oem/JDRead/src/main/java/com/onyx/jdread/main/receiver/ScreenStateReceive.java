package com.onyx.jdread.main.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.onyx.jdread.JDReadApplication;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by li on 2018/2/2.
 */

public class ScreenStateReceive extends BroadcastReceiver {
    public static final String SCREEN_ON = "android.intent.action.SCREEN_ON";
    public static final String SCREEN_OFF = "android.intent.action.SCREEN_OFF";
    public static final String EXTRA_ALARM_ACTION = "com.onyx.jdread.EXTRA_ALARM_ACTION";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SCREEN_ON.equals(intent.getAction())) {
            JDReadApplication.getInstance().lockScreen();
            alarmRegister(context);
        }
    }

    private void alarmRegister(Context context) {
        Intent intent = new Intent(EXTRA_ALARM_ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60*1000, pi);
    }
}
