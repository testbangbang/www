package com.onyx.android.update.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.LogCollection;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.update.log.action.LogReportAction;
import com.onyx.android.update.log.action.LogUploadAction;
import com.onyx.android.update.log.receiver.LogFeedbackReceiver;

/**
 * Created by suicheng on 2018/1/29.
 */
public class MainService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (LogFeedbackReceiver.ACTION_FEEDBACK.equals(action)) {
            processFeedback(intent);
        } else if (LogFeedbackReceiver.ACTION_FEEDBACK_UPLOAD.equals(action)) {
            processFeedbackTriggerUpload(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void processFeedback(Intent intent) {
        String content = intent.getStringExtra(Constant.ARGS_TAG);
        LogReportAction action = new LogReportAction(JSONObjectParseUtils.parseObject(content, LogCollection.class));
        action.execute(getApplicationContext(), null);
    }

    private void processFeedbackTriggerUpload(Intent intent) {
        LogUploadAction action = new LogUploadAction(null);
        action.execute(getApplicationContext(), null);
    }
}
