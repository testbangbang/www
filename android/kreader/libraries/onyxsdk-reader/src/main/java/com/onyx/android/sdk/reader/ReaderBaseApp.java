package com.onyx.android.sdk.reader;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

/**
 * Created by Joy on 2016/4/15.
 */
public class ReaderBaseApp extends MultiDexApplication {
    private static ReaderBaseApp instance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(ReaderBaseApp.this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static ReaderBaseApp instance() {
        return instance;
    }

}
