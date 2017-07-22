package com.onyx.android.note;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.view.View;

import com.onyx.android.note.utils.NoteAppConfig;
import com.onyx.android.note.utils.NotePreference;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.OnyxDownloadManager;
import com.onyx.android.sdk.data.utils.CloudConf;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.ui.compat.AppCompatImageViewCollection;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.config.ShapeGeneratedDatabaseHolder;
import com.squareup.leakcanary.LeakCanary;

import static com.onyx.android.sdk.utils.DeviceUtils.exit;

/**
 * Created by zhuzeng on 6/26/16.
 */
public class NoteApplication extends MultiDexApplication {
    static final boolean DEBUG_MEMORY_LEAK = true;

    private NoteViewHelper noteViewHelper;
    private static NoteApplication instance;
    private CloudStore cloudStore;
    private OnyxDownloadManager downloadManager;
    private DataManager dataManager;

    public NoteViewHelper getNoteViewHelper() {
        if (noteViewHelper == null) {
            noteViewHelper = new NoteViewHelper();
        }
        return noteViewHelper;
    }

    public static void initWithAppConfig(final Activity activity) {
        DeviceUtils.setFullScreenOnCreate(activity, NoteAppConfig.sharedInstance(activity).useFullScreen());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initCloudStore();
        initDownloadManager();
        initDataManager();
        initDataProvider();
        installExceptionHandler();
        initCompatColorImageConfig();
        if (DEBUG_MEMORY_LEAK) {
            LeakCanary.install(this);
        }
    }

    private void initDataManager() {
        dataManager = new DataManager();
    }

    private void initCloudStore() {
        cloudStore = new CloudStore();
        cloudStore.setCloudConf(CloudConf.create(Constant.ONYX_HOST_BASE,
                Constant.ONYX_API_BASE,
                Constant.DEFAULT_CLOUD_STORAGE));
    }

    private void initDownloadManager() {
        OnyxDownloadManager.init(this);
        downloadManager =  OnyxDownloadManager.getInstance();
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public CloudManager getCloudManager() {
        return cloudStore.getCloudManager();
    }

    public OnyxDownloadManager getDownloadManager() {
        return downloadManager;
    }

    public static NoteApplication getInstance() {
        return instance;
    }

    public void initDataProvider() {
        FlowConfig.Builder builder = new FlowConfig.Builder(this);
        builder.addDatabaseHolder(ShapeGeneratedDatabaseHolder.class);
        FlowManager.init(builder.build());
        NotePreference.init(this);
    }

    private void installExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException (Thread thread, Throwable e) {
                e.printStackTrace();
                final View view = getNoteViewHelper().getView();
                getNoteViewHelper().reset(view);
                exit();
            }
        });
    }

    private void initCompatColorImageConfig() {
        AppCompatImageViewCollection.setAlignView(AppCompatUtils.isColorDevice(this));
    }
}
