package com.onyx.jdread.library.model;

import android.content.Context;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.DataManager;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by suicheng on 2017/4/15.
 */

public class DataBundle {
    private Context appContext;
    private EventBus eventBus = new EventBus();
    private DataManager dataManager = new DataManager();
    private CloudManager cloudManager = new CloudManager();
    private LibraryViewDataModel libraryViewDataModel = new LibraryViewDataModel(getEventBus());

    public DataBundle(Context appContext) {
        this.appContext = appContext;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public LibraryViewDataModel getLibraryViewDataModel() {
        return libraryViewDataModel;
    }

    public void setLibraryViewDataModel(LibraryViewDataModel libraryViewDataModel) {
        this.libraryViewDataModel = libraryViewDataModel;
    }

    public CloudManager getCloudManager() {
        return cloudManager;
    }

    public void setCloudManager(CloudManager cloudManager) {
        this.cloudManager = cloudManager;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public Context getAppContext() {
        return appContext;
    }
}
