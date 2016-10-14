package com.onyx.kreader.ui;

import android.app.Application;
import android.content.Context;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.kreader.ui.data.SingletonSharedPreference;
import com.raizlabs.android.dbflow.config.DatabaseHolder;
import com.raizlabs.android.dbflow.config.ReaderNoteGeneratedDatabaseHolder;
import com.squareup.leakcanary.LeakCanary;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Joy on 2016/4/15.
 */
public class KReaderApp extends Application {
    private static KReaderApp instance;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        DataManager.init(this, databaseHolderList());
        SingletonSharedPreference.init(this);
        LeakCanary.install(this);
        instance = this;
        context = getApplicationContext();
    }

    public static KReaderApp instance() {
        return instance;
    }

    public static Context getContext() {
        return context;
    }

    private List<Class<? extends DatabaseHolder>> databaseHolderList() {
        List<Class<? extends DatabaseHolder>> list = new ArrayList<>();
        list.add(ReaderNoteGeneratedDatabaseHolder.class);
        return list;
    }

}
