package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by jackdeng on 2018/1/5.
 */

public class VipReadViewModel extends BaseObservable {

    private EventBus eventBus;
    private List<SubjectViewModel> subjectModels;
    private TitleBarViewModel titleBarViewModel;

    public VipReadViewModel(EventBus eventBus) {
        this.eventBus = eventBus;
        titleBarViewModel = new TitleBarViewModel();
        titleBarViewModel.setEventBus(eventBus);
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public List<SubjectViewModel> getSubjectModels() {
        return subjectModels;
    }

    public void setSubjectModels(List<SubjectViewModel> subjectModels) {
        this.subjectModels = subjectModels;
        notifyChange();
    }

    public TitleBarViewModel getTitleBarViewModel() {
        return titleBarViewModel;
    }

    public void setTitleBarViewModel(TitleBarViewModel titleBarViewModel) {
        this.titleBarViewModel = titleBarViewModel;
    }
}