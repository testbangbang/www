package com.onyx.jdread.library.model;

import android.content.Context;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.main.model.MainBundle;
import com.onyx.jdread.setting.model.SettingBundle;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by suicheng on 2017/4/15.
 */

public class LibraryDataBundle extends MainBundle {
    private static LibraryDataBundle bundle;
    private DataManager dataManager = new DataManager();
    private CloudManager cloudManager = new CloudManager();
    private LibraryViewDataModel libraryViewDataModel = new LibraryViewDataModel(getEventBus());
    private SearchBookModel searchBookModel = new SearchBookModel(getEventBus());

    public static LibraryDataBundle getInstance() {
        if (bundle == null) {
            bundle = new LibraryDataBundle(JDReadApplication.getInstance());
        }
        return bundle;
    }

    public LibraryDataBundle(Context appContext) {
        super(appContext);
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

    @Override
    public EventBus getEventBus() {
        return super.getEventBus();
    }

    @Override
    public Context getAppContext() {
        return super.getAppContext();
    }

    public SearchBookModel getSearchBookModel() {
        return searchBookModel;
    }

    public void setSearchBookModel(SearchBookModel model) {
        this.searchBookModel = model;
    }
}
