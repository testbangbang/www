package com.onyx.jdread.shop.model;

import android.databinding.ObservableField;

import com.onyx.jdread.shop.event.VipButtonClickEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jackdeng on 2018/1/19.
 */

public class VipUserInfoViewModel {
    public final ObservableField<String> name = new ObservableField<>();
    public final ObservableField<String> imageUrl = new ObservableField<>();
    public final ObservableField<String> vipStatus = new ObservableField<>();
    public EventBus eventBus;

    public VipUserInfoViewModel(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void onVipButtonClick() {
        getEventBus().post(new VipButtonClickEvent());
    }

    public EventBus getEventBus() {
        return eventBus;
    }
}
