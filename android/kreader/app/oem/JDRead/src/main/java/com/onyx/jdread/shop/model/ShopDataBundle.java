package com.onyx.jdread.shop.model;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.jdread.main.common.AppBaseInfo;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;

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
    private DataManager dataManager;
    private ShopCartModel shopCartModel;
    private RankViewModel rankViewModel;
    private BookDetailResultBean.Detail bookDetail;
    private TitleBarModel titleBarModel;

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

    public RankViewModel getRankViewModel() {
        if (rankViewModel == null) {
            synchronized (RankViewModel.class) {
                if (rankViewModel == null) {
                    rankViewModel = new RankViewModel(getEventBus());
                }
            }
        }
        return rankViewModel;
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

    public DataManager getDataManager() {
        if (dataManager == null) {
            synchronized (DataManager.class) {
                if (dataManager == null) {
                    dataManager = new DataManager();
                }
            }
        }
        return dataManager;
    }

    public ShopCartModel getShopCartModel() {
        if (shopCartModel == null) {
            shopCartModel = new ShopCartModel();
        }
        return shopCartModel;
    }

    public void setBookDetail(BookDetailResultBean.Detail bookDetail) {
        this.bookDetail = bookDetail;
    }

    public BookDetailResultBean.Detail getBookDetail() {
        return bookDetail;
    }

    public TitleBarModel getTitleBarModel() {
        if (titleBarModel == null) {
            titleBarModel = new TitleBarModel(eventBus);
        }
        return titleBarModel;
    }
}
