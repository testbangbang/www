package com.onyx.jdread.shop.model;

import com.onyx.jdread.shop.event.OnRankViewClick;
import com.onyx.jdread.shop.event.OnStoreBakcTopClick;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jackdeng on 2017/12/8.
 */

public class BookStoreViewModel {
    public SubjectViewModel bannerSubjectIems;
    public SubjectViewModel coverSubjectIems;
    public SubjectViewModel titleSubjectIems;
    public SubjectViewModel specialTodaySubjectIems;
    public String searchContent;
    public EventBus eventBus;

    public BookStoreViewModel(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public SubjectViewModel getBannerSubjectIems() {
        return bannerSubjectIems;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void setBannerSubjectIems(SubjectViewModel bannerSubjectIems) {
        this.bannerSubjectIems = bannerSubjectIems;
    }

    public SubjectViewModel getCoverSubjectIems() {
        return coverSubjectIems;
    }

    public void setCoverSubjectIems(SubjectViewModel coverSubjectIems) {
        this.coverSubjectIems = coverSubjectIems;
    }

    public SubjectViewModel getTitleSubjectIems() {
        return titleSubjectIems;
    }

    public void setTitleSubjectIems(SubjectViewModel titleSubjectIems) {
        this.titleSubjectIems = titleSubjectIems;
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
    }

    public void onRankViewClick() {
        getEventBus().post(new OnRankViewClick());
    }

    public void onEnjoyReadViewClick() {

    }

    public void onSaleViewClick() {

    }

    public void onNewBookViewClick() {

    }

    public void onCategoryViewClick() {

    }

    public void onViewAllBookViewClick() {

    }

    public void onBackTopViewClick() {
        getEventBus().post(new OnStoreBakcTopClick());
    }

    public void onShoppingCartViewClick() {

    }
}