package com.onyx.kreader.ui;

import android.app.Application;
import com.onyx.kreader.ui.data.SingletonSharedPreference;

/**
 * Created by Joy on 2016/4/15.
 */
public class KReaderApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        SingletonSharedPreference.init(this);
    }
}
