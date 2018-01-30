package com.onyx.android.update.log.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.onyx.android.update.service.MainService;

/**
 * Created by suicheng on 2018/1/29.
 */
public class LogFeedbackReceiver extends BroadcastReceiver {

    public static final String ACTION = "com.onyx.action.LOG_FEEDBACK";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION.equals(intent.getAction())) {
            startServiceForLogFeedBack(context, intent);
        }
    }

    private void startServiceForLogFeedBack(Context context, Intent intent) {
        intent.setComponent(new ComponentName(context, MainService.class));
        context.startService(intent);
    }
}
