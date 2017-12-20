package com.onyx.jdread.shop.model;

import com.onyx.jdread.common.AppBaseInfo;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jackdeng on 2017/12/8.
 */

public class ShopDataBundle {
    private static ShopDataBundle shopDataBundle;
    private EventBus eventBus;
    private BookShopViewModel shopViewModel;
    private BookDetailViewModel bookDetailViewModel;
    private AppBaseInfo appBaseInfo;

    private ShopDataBundle() {

    }

    public static ShopDataBundle getInstance() {
        if (shopDataBundle == null) {
            synchronized (BookDetailViewModel.class) {
                if (shopDataBundle == null) {
                    shopDataBundle = new ShopDataBundle();
                }
            }
        }
        return shopDataBundle;
    }

    public EventBus getEventBus() {
        if (eventBus == null) {
            eventBus = EventBus.getDefault();
        }
        return eventBus;
    }

    public BookShopViewModel getShopViewModel() {
        if (shopViewModel == null) {
            synchronized (BookShopViewModel.class) {
                if (shopViewModel == null) {
                    shopViewModel = new BookShopViewModel(getEventBus());
                }
            }
        }
        return shopViewModel;
    }

    public BookDetailViewModel getBookDetailViewModel() {
        if (bookDetailViewModel == null) {
            synchronized (BookDetailViewModel.class) {
                if (bookDetailViewModel == null) {
                    bookDetailViewModel = new BookDetailViewModel(getEventBus());
                }
            }
        }
        return bookDetailViewModel;
    }

    public AppBaseInfo getAppBaseInfo() {
        if (appBaseInfo == null) {
            synchronized (AppBaseInfo.class) {
                if (appBaseInfo == null) {
                    appBaseInfo = new AppBaseInfo();
                }
            }
        }
        return appBaseInfo;
    }
}