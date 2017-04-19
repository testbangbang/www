package com.onyx.kreader.ui;

import android.content.Context;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.reader.ReaderBaseApp;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.PackageUtils;
import com.onyx.kreader.BuildConfig;
import com.onyx.android.sdk.ui.compat.AppCompatImageViewCollection;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;
import com.onyx.kreader.ui.data.SingletonSharedPreference;
import com.raizlabs.android.dbflow.config.DatabaseHolder;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
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
        super.onCreate();
        initPl107DeviceConfig();
        DataManager.init(this, databaseHolderList());
        initContentProvider(this);

        SingletonSharedPreference.init(this);
        Debug.setDebug(BuildConfig.DEBUG || DeviceUtils.isEngVersion() || PackageUtils.getAppType(this).equals(PackageUtils.APP_TYPE_DEBUG));
        instance = this;
        Debug.d(getClass(), "onCreate: " + PackageUtils.getAppVersionName(this));
    }

    public static KReaderApp instance() {
        return instance;
    }

    private List<Class<? extends DatabaseHolder>> databaseHolderList() {
        List<Class<? extends DatabaseHolder>> list = new ArrayList<>();
        list.add(ReaderNoteGeneratedDatabaseHolder.class);
        return list;
    }

    static public void initContentProvider(final Context context) {
        try {
            FlowConfig.Builder builder = new FlowConfig.Builder(context);
            FlowManager.init(builder.build());
        } catch (Exception e) {
            if (com.onyx.android.sdk.dataprovider.BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    public void initPl107DeviceConfig() {
        AppCompatImageViewCollection.isPl107Device = AppCompatUtils.isPL107Device(this);
    }

}
