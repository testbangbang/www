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

    public static final String ACTION_FEEDBACK = "com.onyx.action.LOG_FEEDBACK";
    public static final String ACTION_FEEDBACK_UPLOAD = "com.onyx.action.LOG_FEEDBACK_UPLOAD";

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case ACTION_FEEDBACK:
            case ACTION_FEEDBACK_UPLOAD:
                startServiceForLogFeedBack(context, intent);
                break;
        }
    }

    private void startServiceForLogFeedBack(Context context, Intent intent) {
        intent.setComponent(new ComponentName(context, MainService.class));
        context.startService(intent);
    }
}
