package com.onyx.kreader.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by li on 2017/9/21.
 */

public class CheckExaminationResultReceiver extends BroadcastReceiver {
    private OnCheckExamResultListener listener;

    @Override
    public void onReceive(Context context, Intent intent) {
        String result = intent.getStringExtra("check_result");
        if (!StringUtils.isNullOrEmpty(result) && listener != null) {
            listener.checkResult(result);
        }
    }

    public void setOnCheckExamResultListener(OnCheckExamResultListener listener) {
        this.listener = listener;
    }

    public interface OnCheckExamResultListener {
        void checkResult(String result);
    }
}
