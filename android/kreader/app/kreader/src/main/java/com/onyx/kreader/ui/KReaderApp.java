package com.onyx.kreader.ui;

import android.app.Application;
import com.onyx.android.sdk.dataprovider.AsyncDataProvider;
import com.onyx.android.sdk.dataprovider.SharedPreferenceProvider;
import com.raizlabs.android.dbflow.config.DatabaseHolder;
import com.raizlabs.android.dbflow.config.ShapeGeneratedDatabaseHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Joy on 2016/4/15.
 */
public class KReaderApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AsyncDataProvider.init(this, databaseHolderList());
        SharedPreferenceProvider.init(this);
    }

    private List<Class<? extends DatabaseHolder>> databaseHolderList() {
        List<Class<? extends DatabaseHolder>> list = new ArrayList<>();
        list.add(ShapeGeneratedDatabaseHolder.class);
        return list;
    }

}
