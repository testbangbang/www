package com.onyx.android.update.log.action;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.rx.RxRequest;
import com.onyx.android.sdk.rx.RxRequestChain;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.update.UpdateApplication;
import com.onyx.android.update.log.request.RxLogFeedbackRequest;
import com.onyx.android.update.log.request.RxLogFilesLoadRequest;

import java.util.List;

/**
 * Created by suicheng on 2018/1/30.
 */
public class LogUploadAction {

    private List<String> pathList;

    public LogUploadAction(@Nullable List<String> pathList) {
        this.pathList = pathList;
    }

    public void execute(final Context context, final RxCallback rxCallback) {
        if (pathList == null) {
            reportLogFilesByDefault(context, rxCallback);
            return;
        }
        reportLogFiles(context, pathList, rxCallback);
    }

    private void reportLogFilesByDefault(Context context, final RxCallback callback) {
        RxLogFeedbackRequest.setAppContext(context.getApplicationContext());
        final RxLogFilesLoadRequest loadRequest = new RxLogFilesLoadRequest();
        loadRequest.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                reportLogFiles(loadRequest.getAppContext(), loadRequest.getPathList(), callback);
            }
        });
    }

    private void reportLogFiles(@NonNull Context appContext, @NonNull List<String> pathList, final RxCallback rxCallback) {
        if (CollectionUtils.isNullOrEmpty(pathList) || !NetworkUtil.isWiFiConnected(appContext)) {
            return;
        }
        RxRequestChain chain = new RxRequestChain();
        for (String path : pathList) {
            RxLogFeedbackRequest.setAppContext(appContext);
            final RxLogFeedbackRequest feedbackRequest = new RxLogFeedbackRequest(UpdateApplication.getLogOssManger(appContext), path);
            chain.add(feedbackRequest);
        }
        chain.execute(new RxCallback<RxRequest>() {
            @Override
            public void onNext(RxRequest request) {
                RxCallback.invokeNext(rxCallback, request);
            }

            @Override
            public void onError(Throwable e) {
                RxCallback.invokeError(rxCallback, e);
            }

            @Override
            public void onComplete() {
                RxCallback.invokeComplete(rxCallback);
            }
        });
    }
}
