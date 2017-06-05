package com.onyx.android.sdk.data.action.push;

import android.util.Log;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.manager.PushActionManager;
import com.onyx.android.sdk.data.manager.PushActionManager.*;
import com.onyx.android.sdk.data.manager.ScreenSaverManager;
import com.onyx.android.sdk.data.model.common.ScreenSaverConfig;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by suicheng on 2017/5/26.
 */

public class PushScreenSaverAction extends PushAction {
    private static final String TAG = PushScreenSaverAction.class.getSimpleName();

    public static class PushScreenSaverModel {
        public String imageUrl;
        public String imageFileName;
        public int index;

        public boolean convertToGrayScale = true;

        public boolean isAllSet() {
            return index < 0;
        }
    }

    public  PushScreenSaverAction() {
    }

    @Override
    public void execute(PushActionContext actionContext) {
        String data = PushActionManager.getPushData(actionContext.intent);
        if (StringUtils.isNullOrEmpty(data)) {
            return;
        }
        PushScreenSaverModel model = JSONObjectParseUtils.parseObject(data, PushScreenSaverModel.class);
        if (model == null) {
            Log.w(TAG, "NULL PushScreenSaverModel detected");
            return;
        }
        processPushModel(actionContext, model);
    }

    private void processPushModel(final PushActionContext actionContext, final PushScreenSaverModel model) {
        final ScreenSaverConfig config = ScreenSaverManager.getScreenSaverConfig().copy();
        final String filePath = ScreenSaverManager.getSourcePicPath(config, model.imageFileName);
        config.convertToGrayScale = model.convertToGrayScale;
        config.sourcePicPathString = filePath;
        BaseDownloadTask task = OnyxDownloadManager.getInstance().download(actionContext.context, model.imageUrl,
                filePath, model.imageUrl, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (e != null) {
                            return;
                        }
                        setScreenSaver(actionContext, config, model);
                    }
                });
        OnyxDownloadManager.getInstance().startDownload(task);
    }

    private void setScreenSaver(PushActionContext actionContext, ScreenSaverConfig config, PushScreenSaverModel model) {
        if (model.isAllSet()) {
            ScreenSaverManager.setAllScreenSaver(actionContext.context, actionContext.dataManager,
                    config, null);
        } else {
            config.targetPicPathString = config.createTargetPicPath(config.screenSaverInitialNumber + model.index);
            ScreenSaverManager.setOneScreenSaver(actionContext.context, actionContext.dataManager,
                    config, null);
        }
    }
}
