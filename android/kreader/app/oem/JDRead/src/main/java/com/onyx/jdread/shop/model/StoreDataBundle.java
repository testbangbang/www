package com.onyx.jdread.shop.model;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.DataManager;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jackdeng on 2017/12/8.
 */

public class StoreDataBundle {
    private EventBus eventBus = new EventBus();
    private DataManager dataManager = new DataManager();
    private CloudManager cloudManager = new CloudManager();
    private BookStoreViewModel storeViewModel = new BookStoreViewModel(getEventBus());

    public StoreDataBundle() {

    }

    public DataManager getDataManager() {
        return dataManager;
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

    public void setStoreViewModel(BookStoreViewModel storeViewModel){
        this.storeViewModel = storeViewModel;
    }

    public BookStoreViewModel getStoreViewModel() {
        return storeViewModel;
    }
}
