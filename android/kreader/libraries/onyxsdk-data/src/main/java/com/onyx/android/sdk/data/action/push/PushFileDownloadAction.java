package com.onyx.android.sdk.data.action.push;

import android.os.Environment;
import android.util.Log;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.action.ActionContext;
import com.onyx.android.sdk.data.model.BaseData;
import com.onyx.android.sdk.data.model.v2.PushFileEvent;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;

/**
 * Created by suicheng on 2017/8/2.
 */
public class PushFileDownloadAction {

    private PushFileEvent pushFile;
    private boolean md5CheckSuccess;

    public boolean isMd5CheckSuccess() {
        return md5CheckSuccess;
    }

    public PushFileDownloadAction(PushFileEvent pushFile) {
        this.pushFile = pushFile;
    }

    public void execute(final ActionContext actionContext, final BaseCallback baseCallback) {
        if (pushFile == null || StringUtils.isNullOrEmpty(pushFile.url)) {
            return;
        }
        if (StringUtils.isNullOrEmpty(pushFile.filePath)) {
            Log.w("PushFileDownloadAction", "detect the filePath isn't initial");
            return;
        }
        final DownloadAction downloadAction = new DownloadAction(pushFile.url, pushFile.filePath,
                pushFile.url, pushFile.md5);
        downloadAction.execute(actionContext, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                processMd5Result(downloadAction);
                BaseData.asyncSave(pushFile);
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }

    private void processMd5Result(DownloadAction action) {
        this.md5CheckSuccess = action.isMd5CheckSuccess();
    }

    public static String getDefaultPushDir() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Push");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }
}
