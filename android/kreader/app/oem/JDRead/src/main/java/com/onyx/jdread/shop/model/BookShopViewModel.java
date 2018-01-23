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

import java.util.List;

/**
 * Created by jackdeng on 2017/12/8.
 */

public class BookShopViewModel extends BaseObservable {
    public BannerViewModel bannerSubjectIems;
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

    public BannerViewModel getBannerSubjectIems() {
        return bannerSubjectIems;
    }

    public void setBannerSubjectIems(BannerViewModel bannerSubjectIems) {
        this.bannerSubjectIems = bannerSubjectIems;
        notifyChange();
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

    public void setCommonSubjcet(List<SubjectViewModel> commonSubjcet) {
        this.commonSubjcet = commonSubjcet;
        if (commonSubjcet != null) {
            for (int i = 0; i < commonSubjcet.size(); i++) {
                if (i >= Constants.SHOP_MAIN_INDEX_THREE && i % 2 == 1) {
                    SubjectViewModel subjectViewModel = commonSubjcet.get(i);
                    SubjectViewModel nextSubjectViewModel = commonSubjcet.get(i + 1);
                    subjectViewModel.setShowNextTitle(true);
                    subjectViewModel.setModelBeanNext(nextSubjectViewModel.getModelBean());
                }
            }
        }
        notifyChange();
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