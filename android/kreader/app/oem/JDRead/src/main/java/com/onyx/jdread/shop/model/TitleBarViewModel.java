package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;

import com.onyx.jdread.shop.event.OnTopBackEvent;
import com.onyx.jdread.shop.event.OnTopRightEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jackdeng on 2017/12/30.
 */

public class TitleBarViewModel extends BaseObservable{

    public EventBus eventBus;
    public String leftText;
    public String rightText;
    public boolean showRightText;
    public int pageTag;

    public void onLeftTitleClick() {
        getEventBus().post(new OnTopBackEvent(pageTag));
    }

    public void onRightTitleClick() {
        getEventBus().post(new OnTopRightEvent(pageTag));
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }
}