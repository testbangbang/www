package com.onyx.jdread.util;

import android.content.Context;
import android.content.Intent;

import com.onyx.android.sdk.data.Constant;

import java.util.Map;

/**
 * Created by suicheng on 2018/1/30.
 */
public class BroadcastHelper {
    public static final String ACTION_FEEDBACK = "com.onyx.action.LOG_FEEDBACK";
    public static final String ACTION_FEEDBACK_UPLOAD = "com.onyx.action.LOG_FEEDBACK_UPLOAD";

    public static void sendFeedbackBroadcast(Context context, String data) {
        Intent intent = intentWith(ACTION_FEEDBACK, Constant.ARGS_TAG, data);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        context.sendBroadcast(intent);
    }

    public static void sendFeedbackUploadBroadcast(Context context) {
        Intent intent = new Intent(ACTION_FEEDBACK_UPLOAD);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        context.sendBroadcast(intent);
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
