package com.onyx.jdread.shop.model;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.jdread.main.common.AppBaseInfo;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

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
    private TitleBarModel titleBarModel;
    private BuyReadVipModel buyReadVipModel;
    private BookDetailResultBean.DetailBean bookDetail;
    private ViewAllViewModel viewAllViewModel;
    private BookSaleViewModel bookSaleViewModel;
    private NewBookViewModel newBookViewModel;
    private VipReadViewModel vipReadViewModel;
    private List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> categoryBean;
    private VipUserInfoViewModel vipUserInfoViewModel;
    private PayOrderViewModel payOrderViewModel;

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

    public VipUserInfoViewModel getVipUserInfoViewModel() {
        if (vipUserInfoViewModel == null) {
            synchronized (VipUserInfoViewModel.class) {
                if (vipUserInfoViewModel == null) {
                    vipUserInfoViewModel = new VipUserInfoViewModel(getEventBus());
                }
            }
        }
        return vipUserInfoViewModel;
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

    public NewBookViewModel getNewBookViewModel() {
        if (newBookViewModel == null) {
            synchronized (NewBookViewModel.class) {
                if (newBookViewModel == null) {
                    newBookViewModel = new NewBookViewModel(getEventBus());
                }
            }
        }
        return newBookViewModel;
    }

    public BookSaleViewModel getBookSaleViewModel() {
        if (bookSaleViewModel == null) {
            synchronized (BookSaleViewModel.class) {
                if (bookSaleViewModel == null) {
                    bookSaleViewModel = new BookSaleViewModel(getEventBus());
                }
            }
        }
        return bookSaleViewModel;
    }

    public VipReadViewModel getVipReadViewModel() {
        if (vipReadViewModel == null) {
            synchronized (VipReadViewModel.class) {
                if (vipReadViewModel == null) {
                    vipReadViewModel = new VipReadViewModel(getEventBus());
                }
            }
        }
        return vipReadViewModel;
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

    public ViewAllViewModel getViewAllViewModel() {
        if (viewAllViewModel == null) {
            synchronized (ViewAllViewModel.class) {
                if (viewAllViewModel == null) {
                    viewAllViewModel = new ViewAllViewModel(getEventBus());
                }
            }
        }
        return viewAllViewModel;
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

    public void setBookDetail(BookDetailResultBean.DetailBean bookDetail) {
        this.bookDetail = bookDetail;
    }

    public BookDetailResultBean.DetailBean getBookDetail() {
        return bookDetail;
    }

    public TitleBarModel getTitleBarModel() {
        if (titleBarModel == null) {
            titleBarModel = new TitleBarModel(eventBus);
        }
        return titleBarModel;
    }

    public BuyReadVipModel getBuyReadVipModel() {
        if (buyReadVipModel == null) {
            buyReadVipModel = new BuyReadVipModel(getEventBus());
        }
        return buyReadVipModel;
    }

    public PayOrderViewModel getPayOrderViewModel() {
        if (payOrderViewModel == null) {
            payOrderViewModel = new PayOrderViewModel(getEventBus());
        }
        return payOrderViewModel;
    }

    public void setCategoryBean(List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> categoryBean) {
        this.categoryBean = categoryBean;
    }

    public List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> getCategoryBean() {
        return categoryBean;
    }
}
