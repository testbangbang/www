package com.onyx.jdread.shop.model;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.DataManager;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jackdeng on 2017/12/8.
 */

public class ShopDataBundle {
    private EventBus eventBus = EventBus.getDefault();
    private DataManager dataManager = new DataManager();
    private CloudManager cloudManager = new CloudManager();
    private BookShopViewModel shopViewModel = new BookShopViewModel(getEventBus());
    private BookDetailViewModel bookDetailViewModel = new BookDetailViewModel(getEventBus());

    public ShopDataBundle() {

    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public CloudManager getCloudManager() {
        return cloudManager;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public BookShopViewModel getShopViewModel() {
        return shopViewModel;
    }

    public BookDetailViewModel getBookDetailViewModel() {
        return bookDetailViewModel;
    }
}
