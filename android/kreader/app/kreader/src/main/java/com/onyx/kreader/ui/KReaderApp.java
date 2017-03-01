package com.onyx.kreader.ui;

import android.content.Context;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.reader.ReaderBaseApp;
import com.onyx.android.sdk.reader.common.Debug;
import com.onyx.kreader.ui.data.SingletonSharedPreference;
import com.raizlabs.android.dbflow.config.DatabaseHolder;
import com.raizlabs.android.dbflow.config.ReaderNoteGeneratedDatabaseHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Joy on 2016/4/15.
 */
public class KReaderApp extends ReaderBaseApp {
    private static KReaderApp instance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        Debug.d(getClass(), "onCreate");
        super.onCreate();
        DataManager.init(this, databaseHolderList());
        SingletonSharedPreference.init(this);
//        LeakCanary.install(this);
        instance = this;
    }

    @Override
    public void onTerminate() {
        Debug.d(getClass(), "onTerminate");
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        Debug.e(getClass(), "onLowMemory");
        super.onLowMemory();
    }

    public static KReaderApp instance() {
        return instance;
    }

    private List<Class<? extends DatabaseHolder>> databaseHolderList() {
        List<Class<? extends DatabaseHolder>> list = new ArrayList<>();
        list.add(ReaderNoteGeneratedDatabaseHolder.class);
        return list;
    }

}
