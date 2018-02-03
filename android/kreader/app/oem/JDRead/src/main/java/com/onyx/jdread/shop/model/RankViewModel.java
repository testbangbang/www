package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by jackdeng on 2018/1/5.
 */

public class RankViewModel extends BaseObservable {

    private EventBus eventBus;
    private List<BaseSubjectViewModel> rankItems;
    private TitleBarViewModel titleBarViewModel;
    private int totalPages = 1;

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public RankViewModel(EventBus eventBus) {
        this.eventBus = eventBus;
        titleBarViewModel = new TitleBarViewModel();
        titleBarViewModel.setEventBus(eventBus);
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public List<BaseSubjectViewModel> getRankItems() {
        return rankItems;
    }

    public TitleBarViewModel getTitleBarViewModel() {
        return titleBarViewModel;
    }

    public void setRankItems(List<BaseSubjectViewModel> rankItems) {
        this.rankItems = rankItems;
        notifyChange();
    }

    public void setTitleBarViewModel(TitleBarViewModel titleBarViewModel) {
        this.titleBarViewModel = titleBarViewModel;
    }
}