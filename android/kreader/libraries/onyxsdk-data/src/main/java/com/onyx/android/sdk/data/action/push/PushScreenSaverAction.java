package com.onyx.android.sdk.data.action.push;

import android.content.Intent;
import android.util.Log;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.manager.PushActionManager;
import com.onyx.android.sdk.data.manager.PushActionManager.*;
import com.onyx.android.sdk.data.model.common.ScreenSaverConfig;
import com.onyx.android.sdk.data.request.common.ScreenSaverRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;

/**
 * Created by suicheng on 2017/5/26.
 */

public class PushScreenSaverAction extends PushAction {
    public static int SCREEN_SAVER_COUNT_LIMIT = 3;

    private ScreenSaverConfig config;
    private String imageFilePath;

    public static class PushScreenSaverModel {
        public String imageUrl;
        public String imageFileName;
        public int index;

        public boolean convertToBlackWhite = true;

        public boolean isAllSet() {
            return index < 0;
        }
    }

    public PushScreenSaverAction(ScreenSaverConfig config, String imageFilePath) {
        this.config = config;
        this.imageFilePath = imageFilePath;
    }

    @Override
    public void execute(PushActionContext actionContext) {
        String data = PushActionManager.getPushData(actionContext.intent);
        if (StringUtils.isNullOrEmpty(data)) {
            return;
        }
        PushScreenSaverModel model = JSONObjectParseUtils.parseObject(data, PushScreenSaverModel.class);
        if (model != null) {
            processPushModel(actionContext, model);
        }
    }

    private String getImageFilePath(PushScreenSaverModel model) {
        File file = new File(imageFilePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (file.isDirectory()) {
            return new File(imageFilePath, model.imageFileName).getAbsolutePath();
        } else if (file.isFile()) {
            if (StringUtils.isNullOrEmpty(model.imageFileName)) {
                return imageFilePath;
            }
            return new File(FileUtils.getParent(imageFilePath), model.imageFileName).getAbsolutePath();
        }
        return imageFilePath;
    }

    private void processPushModel(final PushActionContext actionContext, final PushScreenSaverModel model) {
        config.convertToBlackWhite = model.convertToBlackWhite;
        final String filePath = getImageFilePath(model);
        BaseDownloadTask task = OnyxDownloadManager.getInstance().download(actionContext.context, model.imageUrl,
                filePath, model.imageUrl, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (e != null) {
                            return;
                        }
                        if (model.isAllSet()) {
                            setAllScreenSaver(actionContext, config, new File(filePath));
                        } else {
                            setOneScreenSaver(actionContext, config.screenSaverInitialNumber + model.index, new File(filePath));
                        }
                    }
                });
        OnyxDownloadManager.getInstance().startDownload(task);
    }

    private void setAllScreenSaver(final PushActionContext actionContext, ScreenSaverConfig config, File file) {
        for (int i = config.screenSaverInitialNumber; i < config.screenSaverInitialNumber + SCREEN_SAVER_COUNT_LIMIT; i++) {
            setOneScreenSaver(actionContext, i, file);
        }
    }

    private void setOneScreenSaver(final PushActionContext actionContext, int index, File file) {
        config.targetPicPathString = config.createTargetPicPath(index);
        config.sourcePicPathString = file.getAbsolutePath();
        ScreenSaverRequest saverRequest = new ScreenSaverRequest(config);
        actionContext.dataManager.submit(actionContext.context, saverRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null || request.isAbort()) {
                    return;
                }
                Log.i("screenSaver", "success");
                Intent intent = new Intent("update_standby_pic");
                actionContext.context.sendBroadcast(intent);
            }
        });
    }
}
