package com.onyx.android.update.log.request;

import android.content.Context;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by suicheng on 2018/1/29.
 */
public class RxLogFilesLoadRequest extends RxBaseCloudRequest {

    private List<String> pathList;

    public RxLogFilesLoadRequest() {
    }

    public List<String> getPathList() {
        return pathList;
    }

    @Override
    public RxBaseCloudRequest call() throws Exception {
        pathList = loadLogFilePathList(getAppContext());
        return this;
    }

    private List<String> loadLogFilePathList(Context context) {
        List<String> pathList = new ArrayList<>();
        Collection<File> fileList = LogUtils.getLogFiles(context.getFilesDir().getAbsolutePath());
        if (!CollectionUtils.isNullOrEmpty(fileList)) {
            for (File file : fileList) {
                pathList.add(file.getAbsolutePath());
            }
        }
        return pathList;
    }
}
