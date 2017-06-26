package com.onyx.android.dr;

import android.app.Application;

import com.onyx.android.sdk.data.DataManager;
import com.raizlabs.android.dbflow.config.DRGeneratedDatabaseHolder;
import com.raizlabs.android.dbflow.config.DatabaseHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehai on 17-6-26.
 */

public class DRApplication extends Application {

    private static DRApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        initConfig();
    }

    private void initConfig() {
        try {
            sInstance = this;
            DataManager.init(sInstance.getApplicationContext(), databaseHolderList());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DRApplication getInstance(){
        return sInstance;
    }

    private List<Class<? extends DatabaseHolder>> databaseHolderList() {
        List<Class<? extends DatabaseHolder>> list = new ArrayList<>();
        list.add(DRGeneratedDatabaseHolder.class);
        return list;
    }
}
