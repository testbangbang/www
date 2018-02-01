package com.onyx.jdread.shop.model;

import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.event.GoShopingCartEvent;
import com.onyx.jdread.shop.event.MainConfigDataChangeEvent;
import com.onyx.jdread.shop.event.SearchViewClickEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by jackdeng on 2017/12/8.
 */

public class BookShopViewModel extends BaseSubjectViewModel {
    public BannerViewModel bannerViewModel;
    public TopFunctionViewModel topFunctionViewModel;
    public List<SubjectViewModel> commonSubjcet;
    public AllCategoryViewModel allCategoryViewModel;
    public List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> titleSubjectItems;
    public String searchContent;
    public EventBus eventBus;
    public List<BaseSubjectViewModel> mainConfigSubjcet;
    private MainConfigEndViewModel endViewModel;

    public List<BaseSubjectViewModel> getMainConfigSubjcet() {
        return mainConfigSubjcet;
    }

    public void setMainConfigSubjcet(List<BaseSubjectViewModel> mainConfigSubjcet) {
        this.mainConfigSubjcet = mainConfigSubjcet;
        getEventBus().post(new MainConfigDataChangeEvent());
        notifyChange();
    }

    public AllCategoryViewModel getAllCategoryViewModel() {
        if (allCategoryViewModel == null) {
            allCategoryViewModel = new AllCategoryViewModel(getEventBus());
        }
        return allCategoryViewModel;
    }

    public TopFunctionViewModel getTopFunctionViewModel() {
        if (topFunctionViewModel == null) {
            topFunctionViewModel = new TopFunctionViewModel(getEventBus());
        }
        return topFunctionViewModel;
    }

    public MainConfigEndViewModel getMainConfigEndViewModel() {
        if (endViewModel == null) {
            endViewModel = new MainConfigEndViewModel(getEventBus());
        }
        return endViewModel;
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

    public void onShoppingCartViewClick() {
        getEventBus().post(new GoShopingCartEvent());
    }

    public void onSearchViewClick() {
        getEventBus().post(new SearchViewClickEvent());
    }
}