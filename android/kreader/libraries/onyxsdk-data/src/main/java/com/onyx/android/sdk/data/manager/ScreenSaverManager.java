package com.onyx.android.sdk.data.manager;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.common.ScreenSaverConfig;
import com.onyx.android.sdk.data.request.common.ScreenSaverRequest;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;

/**
 * Created by suicheng on 2017/6/5.
 */
public class ScreenSaverManager {
    public static int SCREEN_SAVER_COUNT_LIMIT = 3;

    private static ScreenSaverManager globalManager;
    private ScreenSaverConfig globalConfig;

    private ScreenSaverManager() {
    }

    public static ScreenSaverManager init(ScreenSaverConfig config) {
        globalManager = new ScreenSaverManager();
        globalManager.globalConfig = config;
        return globalManager;
    }

    public static ScreenSaverManager getInstance() {
        return globalManager;
    }

    public static ScreenSaverConfig getScreenSaverConfig() {
        return globalManager.globalConfig;
    }

    public static String getSourcePicPath(ScreenSaverConfig config, String targetFileName) {
        File file = new File(config.sourcePicPathString);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (file.isDirectory()) {
            return new File(config.sourcePicPathString, targetFileName).getAbsolutePath();
        } else if (file.isFile()) {
            if (StringUtils.isNullOrEmpty(targetFileName)) {
                return config.sourcePicPathString;
            }
            return new File(FileUtils.getParent(config.sourcePicPathString), targetFileName).getAbsolutePath();
        }
        return config.sourcePicPathString;
    }

    public static void setAllScreenSaver(final Context context, final DataManager dataManager,
                                         ScreenSaverConfig config, BaseCallback baseCallback) {
        for (int i = config.screenSaverInitialNumber; i < config.screenSaverInitialNumber + SCREEN_SAVER_COUNT_LIMIT; i++) {
            ScreenSaverConfig newConfig = config.copy();
            newConfig.targetPicPathString = newConfig.createTargetPicPath(i);
            setOneScreenSaver(context, dataManager, newConfig, baseCallback);
        }
    }

    public static void setOneScreenSaver(final Context context, final DataManager dataManager,
                                         ScreenSaverConfig config, final BaseCallback baseCallback) {
        ScreenSaverRequest saverRequest = new ScreenSaverRequest(config);
        dataManager.submit(context, saverRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }
}
