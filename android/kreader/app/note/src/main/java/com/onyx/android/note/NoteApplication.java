package com.onyx.android.note;

import android.app.Application;
import android.content.Context;
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

    @Override
    public void onCreate() {
        super.onCreate();
        initDataProvider(this);
    }

    private void initDataProvider(final Context context) {
        FlowConfig.Builder builder = new FlowConfig.Builder(context);
        builder.addDatabaseHolder(ShapeGeneratedDatabaseHolder.class);
        FlowManager.init(builder.build());

    }
}
