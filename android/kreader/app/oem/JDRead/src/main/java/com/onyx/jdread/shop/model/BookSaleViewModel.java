package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by jackdeng on 2018/1/5.
 */

public class BookSaleViewModel extends BaseObservable {

    private EventBus eventBus;
    private List<BaseSubjectViewModel> subjectModels;
    private TitleBarViewModel titleBarViewModel;
    private VipUserInfoViewModel userInfoViewModel;
    private int totalPages = 1;

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public BookSaleViewModel(EventBus eventBus) {
        this.eventBus = eventBus;
        titleBarViewModel = new TitleBarViewModel();
        titleBarViewModel.setEventBus(eventBus);
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public List<BaseSubjectViewModel> getSubjectModels() {
        return subjectModels;
    }

    public void setSubjectModels(List<BaseSubjectViewModel> subjectModels) {
        this.subjectModels = subjectModels;
        notifyChange();
    }

    public TitleBarViewModel getTitleBarViewModel() {
        return titleBarViewModel;
    }

    public VipUserInfoViewModel getUserInfoViewModel() {
        return userInfoViewModel;
    }

    public void setUserInfoViewModel(VipUserInfoViewModel userInfoViewModel) {
        this.userInfoViewModel = userInfoViewModel;
        notifyChange();
    }
}