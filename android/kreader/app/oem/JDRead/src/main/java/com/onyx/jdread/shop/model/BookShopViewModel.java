package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;

import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.event.CategoryViewClick;
import com.onyx.jdread.shop.event.EnjoyReadViewClick;
import com.onyx.jdread.shop.event.GoShopingCartEvent;
import com.onyx.jdread.shop.event.NewBookViewClick;
import com.onyx.jdread.shop.event.RankViewClick;
import com.onyx.jdread.shop.event.SaleViewClick;
import com.onyx.jdread.shop.event.SearchViewClickEvent;
import com.onyx.jdread.shop.event.ShopBakcTopClick;
import com.onyx.jdread.shop.event.ShopMainViewAllBookEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackdeng on 2017/12/8.
 */

public class BookShopViewModel extends BaseObservable {
    public BannerViewModel bannerViewModel;
    public List<SubjectViewModel> commonSubjcet;
    public AllCategoryViewModel allCategoryViewModel;
    public List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> titleSubjectItems;
    public String searchContent;
    public EventBus eventBus;

    public AllCategoryViewModel getAllCategoryViewModel() {
        if (allCategoryViewModel == null) {
            allCategoryViewModel = new AllCategoryViewModel(getEventBus());
        }
        return allCategoryViewModel;
    }

    public BookShopViewModel(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public BannerViewModel getBannerViewModel() {
        if (bannerViewModel == null) {
            this.bannerViewModel = new BannerViewModel(getEventBus());
        }
        return bannerViewModel;
    }

    public String getSearchContent() {
        return searchContent;
    }

    public void setSearchContent(String searchContent) {
        this.searchContent = searchContent;
    }

    public List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> getCategorySubjectItems() {
        return titleSubjectItems;
    }

    public void setCategorySubjectItems(List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> categorySubjectItems) {
        this.titleSubjectItems = categorySubjectItems;
        notifyChange();
    }

    public List<SubjectViewModel> getCommonSubjcet() {
        return commonSubjcet;
    }

    public void setCommonSubjcet() {
        if (commonSubjcet != null && commonSubjcet.size() >= Constants.SHOP_MAIN_INDEX_EIGHT) {
            return;
        }
        commonSubjcet= new ArrayList<>();
        for (int i = 0; i < Constants.SHOP_MAIN_INDEX_NINE; i++) {
            SubjectViewModel subjectViewModel = new SubjectViewModel();
            subjectViewModel.setEventBus(getEventBus());
            commonSubjcet.add(subjectViewModel);
        }
    }

    public void onRankViewClick() {
        getEventBus().post(new RankViewClick());
    }

    public void onEnjoyReadViewClick() {
        getEventBus().post(new EnjoyReadViewClick());
    }

    public void onSaleViewClick() {
        getEventBus().post(new SaleViewClick());
    }

    public void onNewBookViewClick() {
        getEventBus().post(new NewBookViewClick());
    }

    public void onCategoryViewClick() {
        getEventBus().post(new CategoryViewClick());
    }

    public void onViewAllBookViewClick() {
        getEventBus().post(new ShopMainViewAllBookEvent());
    }

    public void onBackTopViewClick() {
        getEventBus().post(new ShopBakcTopClick());
    }

    public void onShoppingCartViewClick() {
        getEventBus().post(new GoShopingCartEvent());
    }

    public void onSearchViewClick() {
        getEventBus().post(new SearchViewClickEvent());
    }
}