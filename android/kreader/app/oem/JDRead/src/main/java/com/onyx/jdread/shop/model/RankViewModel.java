package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;

import com.onyx.jdread.main.common.Constants;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
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

    public void setRankItems() {
        if (rankItems != null && rankItems.size() >= Constants.SHOP_MAIN_INDEX_SIX) {
            return;
        }
        rankItems = new ArrayList<>();
        for (int i = 0; i < Constants.SHOP_MAIN_INDEX_SIX; i++) {
            SubjectViewModel subjectViewModel = new SubjectViewModel();
            subjectViewModel.setEventBus(getEventBus());
            rankItems.add(subjectViewModel);
        }
    }

    public void setTitleBarViewModel(TitleBarViewModel titleBarViewModel) {
        this.titleBarViewModel = titleBarViewModel;
    }
}