package com.onyx.android.update.log.action;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.onyx.android.sdk.data.model.LogCollection;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.update.log.request.RxLogFilesSaveRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2018/1/29.
 */
public class LogReportAction {

    private LogCollection logCollection;
    private boolean reportOnce = false;

    public LogReportAction(@Nullable LogCollection collection) {
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
        collectFilesToReport(context, rxCallback);
    }

    private void collectFilesToReport(final Context context, final RxCallback rxCallback) {
        final RxLogFilesSaveRequest logCollectionRequest = new RxLogFilesSaveRequest(logCollection);
        RxLogFilesSaveRequest.setAppContext(context.getApplicationContext());
        logCollectionRequest.execute(new RxCallback<RxLogFilesSaveRequest>() {
            @Override
            public void onNext(RxLogFilesSaveRequest request) {
                if (reportOnce) {
                    reportLogFile(context, logCollectionRequest.getLogFilePath(), rxCallback);
                    return;
                }
                reportLogFileList(context, rxCallback);
            }

            @Override
            public void onError(Throwable e) {
                RxCallback.invokeError(rxCallback, e);
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
        LogUploadAction uploadAction = new LogUploadAction(list);
        uploadAction.execute(context, rxCallback);
    }

    private void reportLogFileList(@NonNull Context context, final RxCallback rxCallback) {
        LogUploadAction uploadAction = new LogUploadAction(null);
        uploadAction.execute(context, rxCallback);
    }
}
