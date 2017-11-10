package com.onyx.android.update;

import android.app.Application;

import com.onyx.download.onyxdownloadservice.DownloadTaskManager;

/**
 * Created by huxiaomao on 17/10/23.
 */

public class UpdateApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DownloadTaskManager.getInstance(getApplicationContext());
    }
}
