package com.onyx.android.sun;

import android.app.Application;
import android.content.Context;

import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sun.common.AppConfigData;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.config.ShapeGeneratedDatabaseHolder;

/**
 * Created by hehai on 17-9-29.
 */

public class SunApplication extends Application {
    private static SunApplication instence;
    private NoteManager noteManager;

    public NoteManager getNoteManager() {
        if (noteManager == null) {
            noteManager = new NoteManager(instence);
        }
        return noteManager;
    }

    public static SunApplication getInstence() {
        return instence;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initContext();
        AppConfigData.loadMainTabData(instence);
        initDataProvider(instence);
    }

    private void initContext() {
        instence = this;
    }

    private void initDataProvider(final Context context) {
        FlowConfig.Builder builder = new FlowConfig.Builder(context);
        builder.addDatabaseHolder(ShapeGeneratedDatabaseHolder.class);
        FlowManager.init(builder.build());
    }
}
