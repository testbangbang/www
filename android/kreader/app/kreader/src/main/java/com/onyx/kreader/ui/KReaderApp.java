package com.onyx.kreader.ui;

import android.app.Application;
import com.onyx.kreader.dataprovider.DataProvider;
import com.onyx.kreader.dataprovider.SharedPreferenceProvider;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by Joy on 2016/4/15.
 */
public class KReaderApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DataProvider.init(this);
        SharedPreferenceProvider.init(this);
    }
}
