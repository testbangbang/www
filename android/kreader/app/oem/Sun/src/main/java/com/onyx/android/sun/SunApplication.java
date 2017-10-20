package com.onyx.android.sun;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sun.common.AppConfigData;
import com.raizlabs.android.dbflow.config.DatabaseHolder;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.config.GeneratedDatabaseHolder;
import java.util.ArrayList;
import java.util.List;

import com.raizlabs.android.dbflow.config.ShapeGeneratedDatabaseHolder;


/**
 * Created by hehai on 17-9-29.
 */

public class SunApplication extends Application {
    private static SunApplication instance;
    private NoteManager noteManager;

    public NoteManager getNoteManager() {
        if (noteManager == null) {
            noteManager = new NoteManager(instance);
        }
        return noteManager;
    }

    public static SunApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initContext();
        MultiDex.install(instance);
        initDatabases(this, databaseHolderList());
        AppConfigData.loadMainTabData(instance);
        OnyxDownloadManager.init(instance);
    }

    private void initContext() {
        instance = this;
    }

    public void initDatabases(final Context context, final List<Class<? extends DatabaseHolder>> list) {
        FlowConfig.Builder builder = new FlowConfig.Builder(context);
        if (list != null) {
            for (Class tClass : list) {
                builder.addDatabaseHolder(tClass);
            }
        }
        FlowManager.init(builder.build());
    }

    private List<Class<? extends DatabaseHolder>> databaseHolderList() {
        List<Class<? extends DatabaseHolder>> list = new ArrayList<>();
        list.add(GeneratedDatabaseHolder.class);
        list.add(ShapeGeneratedDatabaseHolder.class);
        return list;
    }
}
