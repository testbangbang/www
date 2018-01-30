package com.onyx.android.update.log.request;

import com.onyx.android.sdk.data.model.LogCollection;
import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.utils.LogUtils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by suicheng on 2018/1/29.
 */
public class RxLogCollectionRequest extends RxBaseCloudRequest {

    private String logFilePath;
    private LogCollection logCollection;

    public String getLogFilePath() {
        return logFilePath;
    }

    public RxLogCollectionRequest(LogCollection collection) {
        this.logCollection = collection;
    }

    @Override
    public RxLogCollectionRequest call() throws Exception {
        File zipFile = LogUtils.generateFeedBackFile(getAppContext(), JSONObjectParseUtils.toJson(logCollection));
        if (zipFile == null || !zipFile.exists()) {
            throw new FileNotFoundException();
        }
        logFilePath = zipFile.getPath();
        return this;
    }
}
