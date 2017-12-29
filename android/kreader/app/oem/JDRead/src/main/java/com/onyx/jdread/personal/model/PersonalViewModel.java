package com.onyx.jdread.personal.model;

import android.databinding.BaseObservable;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jackdeng on 2017/12/26.
 */

public class PersonalViewModel extends BaseObservable {

    private UserLoginViewModel userLoginViewModel = new UserLoginViewModel();

    public UserLoginViewModel getUserLoginViewModel() {
        userLoginViewModel.setEventBus(getEventBus());
        return userLoginViewModel;
    }

    public EventBus eventBus;

    public PersonalViewModel(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

}