package com.onyx.android.sdk.data.action.push;

import android.util.Log;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.action.ActionContext;
import com.onyx.android.sdk.data.manager.ScreenSaverManager;
import com.onyx.android.sdk.data.model.common.ScreenSaverConfig;
import com.onyx.android.sdk.data.model.v2.PushScreenSaverEvent;

/**
 * Created by suicheng on 2017/5/26.
 */

public class PushScreenSaverAction {
    private static final String TAG = PushScreenSaverAction.class.getSimpleName();

    private PushScreenSaverEvent screenSaverEvent;
    private ScreenSaverConfig screenSaverConfig;

    public PushScreenSaverAction(PushScreenSaverEvent event, ScreenSaverConfig config) {
        this.screenSaverEvent = event;
        this.screenSaverConfig = config;
    }

    public void execute(final ActionContext actionContext, final BaseCallback baseCallback) {
        if (screenSaverEvent == null || screenSaverConfig == null) {
            Log.w(TAG, "NULL ScreenSaverEvent or Config detected");
            return;
        }
        final String filePath = ScreenSaverManager.getSourcePicPath(screenSaverConfig,
                screenSaverEvent.getFileNameWithExtension());
        screenSaverConfig.convertToGrayScale = screenSaverEvent.convertToGrayScale;
        screenSaverConfig.sourcePicPathString = filePath;
        screenSaverEvent.filePath = filePath;
        DownloadAction downloadAction = new DownloadAction(screenSaverEvent.url, filePath, screenSaverEvent.url, "");
        downloadAction.execute(actionContext, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    BaseCallback.invoke(baseCallback, request, e);
                    return;
                }
                setScreenSaver(actionContext, screenSaverConfig, baseCallback);
            }
        });
    }

    private void setScreenSaver(ActionContext actionContext, ScreenSaverConfig config,
                                BaseCallback baseCallback) {
        if (screenSaverEvent.isAllSet()) {
            ScreenSaverManager.setAllScreenSaver(actionContext.context, actionContext.dataManager,
                    config, baseCallback);
        } else {
            config.targetPicPathString = config.createTargetPicPath(config.screenSaverInitialNumber +
                    screenSaverEvent.index);
            ScreenSaverManager.setOneScreenSaver(actionContext.context, actionContext.dataManager,
                    config, baseCallback);
        }
    }
}
