package com.onyx.android.note;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.onyx.android.note.utils.NoteAppConfig;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.config.ShapeGeneratedDatabaseHolder;

/**
 * Created by zhuzeng on 6/26/16.
 */
public class NoteApplication extends Application {


    private static NoteViewHelper noteViewHelper;

    public static NoteViewHelper getNoteViewHelper() {
        if (noteViewHelper == null) {
            noteViewHelper = new NoteViewHelper();
        }
        return noteViewHelper;
    }

    public static void initWithAppConfig(final Activity activity) {
        if (!NoteAppConfig.sharedInstance(activity).useFullScreen()) {
            return;
        }
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initDataProvider(this);
        installExceptionHandler();
    }

    private void initDataProvider(final Context context) {
        FlowConfig.Builder builder = new FlowConfig.Builder(context);
        builder.addDatabaseHolder(ShapeGeneratedDatabaseHolder.class);
        FlowManager.init(builder.build());
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

}
