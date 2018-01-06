package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by jackdeng on 2018/1/5.
 */

public class RankViewModel extends BaseObservable {

    private EventBus eventBus;
    private List<SubjectViewModel> rankItems;
    private TitleBarViewModel titleBarViewModel;

    public RankViewModel(EventBus eventBus) {
        this.eventBus = eventBus;
        titleBarViewModel = new TitleBarViewModel();
        titleBarViewModel.setEventBus(eventBus);
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public List<SubjectViewModel> getRankItems() {
        return rankItems;
    }

    public TitleBarViewModel getTitleBarViewModel() {
        return titleBarViewModel;
    }

    public void setRankItems(List<SubjectViewModel> rankItems) {
        this.rankItems = rankItems;
        notifyChange();
    }

    public void setTitleBarViewModel(TitleBarViewModel titleBarViewModel) {
        this.titleBarViewModel = titleBarViewModel;
    }
}