package com.onyx.android.update.log.action;

import android.content.Context;
import android.support.annotation.NonNull;

import com.onyx.android.sdk.data.model.LogCollection;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.rx.RxRequest;
import com.onyx.android.sdk.rx.RxRequestChain;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.update.UpdateApplication;
import com.onyx.android.update.log.request.RxLogFilesSaveRequest;
import com.onyx.android.update.log.request.RxLogFeedbackRequest;
import com.onyx.android.update.log.request.RxLogFilesLoadRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2018/1/29.
 */
public class LogReportAction {

    private LogCollection logCollection;
    private boolean reportOnce = false;

    public LogReportAction(LogCollection collection) {
        this.logCollection = collection;
        checkLogCollection();
    }

    public void setReportOnce(boolean once) {
        this.reportOnce = once;
    }

    private void checkLogCollection() {
        if (logCollection == null) {
            logCollection = new LogCollection();
        }
        if (StringUtils.isNullOrEmpty(logCollection.desc)) {
            logCollection.desc = "crash bug";
        }
    }

    public void execute(final Context context, final RxCallback rxCallback) {
        final RxLogFilesSaveRequest logCollectionRequest = new RxLogFilesSaveRequest(logCollection);
        RxLogFilesSaveRequest.setAppContext(context.getApplicationContext());
        logCollectionRequest.execute(new RxCallback<RxLogFilesSaveRequest>() {
            @Override
            public void onNext(RxLogFilesSaveRequest request) {
                if (reportOnce) {
                    reportLogFile(context, logCollectionRequest.getLogFilePath(), rxCallback);
                    return;
                }
                loadLogFileList(rxCallback);
            }

            @Override
            public void onError(Throwable e) {
                RxCallback.invokeError(rxCallback, e);
            }
        });
    }

    private void loadLogFileList(final RxCallback callback) {
        final RxLogFilesLoadRequest loadRequest = new RxLogFilesLoadRequest();
        loadRequest.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                reportLogFileList(loadRequest.getAppContext(), loadRequest.getPathList(), callback);
            }
        });
    }

    private void reportLogFile(@NonNull Context context, String path, final RxCallback rxCallback) {
        if (StringUtils.isNullOrEmpty(path)) {
            RxCallback.invokeComplete(rxCallback);
            return;
        }
        List<String> list = new ArrayList<>();
        list.add(path);
        reportLogFileList(context, list, rxCallback);
    }

    private void reportLogFileList(@NonNull Context appContext, @NonNull List<String> pathList, final RxCallback rxCallback) {
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
