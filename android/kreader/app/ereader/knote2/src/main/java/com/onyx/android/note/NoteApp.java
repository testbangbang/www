package com.onyx.android.note;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.config.ShapeGeneratedDatabaseHolder;

/**
 * Created by lxm on 2018/1/31.
 */

public class NoteApp extends MultiDexApplication {

    public static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initDataProvider();
    }

    public void initDataProvider() {
        FlowConfig.Builder builder = new FlowConfig.Builder(this);
        builder.addDatabaseHolder(ShapeGeneratedDatabaseHolder.class);
        FlowManager.init(builder.build());
    }
}
