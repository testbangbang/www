package com.onyx.jdread.main.model;

import android.content.Context;

import com.onyx.jdread.JDReadApplication;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by hehai on 18-1-18.
 */

public class MainBundle {
    private static MainBundle bundle;
    private Context appContext;
    private EventBus eventBus = new EventBus();
    private SystemBarModel systemBarModel;

    public static MainBundle getInstance() {
        if (bundle == null) {
            bundle = new MainBundle(JDReadApplication.getInstance());
        }
        return bundle;
    }

    public MainBundle(Context appContext) {
        this.appContext = appContext;
    }

    public Context getAppContext() {
        return appContext;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public SystemBarModel getSystemBarModel() {
        if (systemBarModel == null) {
            systemBarModel = new SystemBarModel();
        }
        return systemBarModel;
    }
}
