package com.onyx.android.note;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.View;

import com.onyx.android.note.utils.NoteAppConfig;
import com.onyx.android.note.utils.NotePreference;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.ui.compat.AppCompatImageViewCollection;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.config.ShapeGeneratedDatabaseHolder;

/**
 * Created by zhuzeng on 6/26/16.
 */
public class NoteApplication extends Application {


    private static NoteViewHelper noteViewHelper;
    private static NoteApplication instance;

    public static NoteViewHelper getNoteViewHelper() {
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
        initDataProvider(this);
        installExceptionHandler();
        initCompatColorImageConfig();
    }

    public static NoteApplication getInstance() {
        return instance;
    }

    private void initDataProvider(final Context context) {
        FlowConfig.Builder builder = new FlowConfig.Builder(context);
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
            }
        });
    }

    private void initCompatColorImageConfig() {
        AppCompatImageViewCollection.isPl107Device = AppCompatUtils.isPL107Device(this);
    }
}
