package com.onyx.android.plato;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.manager.OTAManager;
import com.onyx.android.sdk.data.utils.CloudConf;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.android.plato.common.AppConfigData;
import com.raizlabs.android.dbflow.config.DatabaseHolder;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.config.ShapeGeneratedDatabaseHolder;
import com.raizlabs.android.dbflow.config.SunGeneratedDatabaseHolder;
import java.util.ArrayList;
import java.util.List;


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
        OTAManager.sharedInstance().getCloudStore().setCloudConf(CloudConf.create(Constant.CN_HOST_BASE, Constant.CN_API_BASE, Constant.DEFAULT_CLOUD_STORAGE));
        PreferenceManager.init(this);
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
        list.add(SunGeneratedDatabaseHolder.class);
        list.add(ShapeGeneratedDatabaseHolder.class);
        return list;
    }
}
