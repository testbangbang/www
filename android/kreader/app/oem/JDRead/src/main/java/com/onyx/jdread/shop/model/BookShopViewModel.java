package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;

import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.event.CategoryViewClick;
import com.onyx.jdread.shop.event.GoShopingCartEvent;
import com.onyx.jdread.shop.event.RankViewClick;
import com.onyx.jdread.shop.event.ShopBakcTopClick;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by jackdeng on 2017/12/8.
 */

public class BookShopViewModel extends BaseObservable {
    public SubjectViewModel bannerSubjectIems;
    public SubjectViewModel coverSubjectOneItems;
    public SubjectViewModel coverSubjectTwoItems;
    public SubjectViewModel coverSubjectThreeItems;
    public SubjectViewModel coverSubjectFourItems;
    public SubjectViewModel coverSubjectFiveItems;
    public SubjectViewModel coverSubjectSixItems;
    public SubjectViewModel titleSubjectIems;
    public SubjectViewModel specialTodaySubjectIems;
    public AllCategoryViewModel allCategoryViewModel;
    public List<CategoryListResultBean.CatListBean> categorySubjectItems;
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

    public SubjectViewModel getBannerSubjectIems() {
        return bannerSubjectIems;
    }

    public void setBannerSubjectIems(SubjectViewModel bannerSubjectIems) {
        this.bannerSubjectIems = bannerSubjectIems;
        notifyChange();
    }

    public SubjectViewModel getCoverSubjectOneItems() {
        return coverSubjectOneItems;
    }

    public void setCoverSubjectOneItems(SubjectViewModel coverSubjectOneItems) {
        this.coverSubjectOneItems = coverSubjectOneItems;
        notifyChange();
    }

    public SubjectViewModel getTitleSubjectIems() {
        return titleSubjectIems;
    }

    public void setTitleSubjectIems(SubjectViewModel titleSubjectIems) {
        this.titleSubjectIems = titleSubjectIems;
        notifyChange();
    }

    public String getSearchContent() {
        return searchContent;
    }

    public void setSearchContent(String searchContent) {
        this.searchContent = searchContent;
    }

    public SubjectViewModel getSpecialTodaySubjectIems() {
        return specialTodaySubjectIems;
    }

    public void setSpecialTodaySubjectIems(SubjectViewModel specialTodaySubjectIems) {
        this.specialTodaySubjectIems = specialTodaySubjectIems;
        notifyChange();
    }

    public List<CategoryListResultBean.CatListBean> getCategorySubjectItems() {
        return categorySubjectItems;
    }

    public void setCategorySubjectItems(List<CategoryListResultBean.CatListBean> categorySubjectItems) {
        this.categorySubjectItems = categorySubjectItems;
        notifyChange();
    }

    public SubjectViewModel getCoverSubjectFourItems() {
        return coverSubjectFourItems;
    }

    public void setCoverSubjectFourItems(SubjectViewModel coverSubjectFourItems) {
        this.coverSubjectFourItems = coverSubjectFourItems;
    }

    public SubjectViewModel getCoverSubjectTwoItems() {
        return coverSubjectTwoItems;
    }

    public void setCoverSubjectTwoItems(SubjectViewModel coverSubjectTwoItems) {
        this.coverSubjectTwoItems = coverSubjectTwoItems;
        notifyChange();
    }

    public SubjectViewModel getCoverSubjectThreeItems() {
        return coverSubjectThreeItems;
    }

    public void setCoverSubjectThreeItems(SubjectViewModel coverSubjectThreeItems) {
        this.coverSubjectThreeItems = coverSubjectThreeItems;
        notifyChange();
    }

    public SubjectViewModel getCoverSubjectFiveItems() {
        return coverSubjectFiveItems;
    }

    public void setCoverSubjectFiveItems(SubjectViewModel coverSubjectFiveItems) {
        this.coverSubjectFiveItems = coverSubjectFiveItems;
        notifyChange();
    }

    public SubjectViewModel getCoverSubjectSixItems() {
        return coverSubjectSixItems;
    }

    public void setCoverSubjectSixItems(SubjectViewModel coverSubjectSixItems) {
        this.coverSubjectSixItems = coverSubjectSixItems;
        notifyChange();
    }

    public void onRankViewClick() {
        getEventBus().post(new RankViewClick());
    }

    public void onEnjoyReadViewClick() {

    }

    public void onSaleViewClick() {

    }

    public void onNewBookViewClick() {

    }

    public void onCategoryViewClick() {
        getEventBus().post(new CategoryViewClick());
    }

    public void onViewAllBookViewClick() {

    }

    public void onBackTopViewClick() {
        getEventBus().post(new ShopBakcTopClick());
    }

    public void onShoppingCartViewClick() {
        getEventBus().post(new GoShopingCartEvent());
    }

    public void onSearchViewClick() {

    }
}