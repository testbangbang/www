package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;

import com.onyx.jdread.shop.event.TopBackEvent;
import com.onyx.jdread.shop.event.TopRightTitle2Event;
import com.onyx.jdread.shop.event.TopRightTitle3Event;
import com.onyx.jdread.shop.event.TopRightTitleEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jackdeng on 2017/12/30.
 */

public class TitleBarViewModel extends BaseObservable{

    public EventBus eventBus;
    public String leftText;
    public String rightText;
    public String rightText2;
    public String rightText3;
    public boolean showRightText;
    public boolean showRightText2;
    public boolean showRightText3;
    public final ObservableInt rightText2IconId =new ObservableInt();
    public final ObservableInt rightText3IconId =new ObservableInt();
    public final ObservableBoolean showTitleBar =new ObservableBoolean(true);
    public int pageTag;

    public void onLeftTitleClick() {
        getEventBus().post(new TopBackEvent(pageTag));
    }

    public void onRightTitleClick() {
        getEventBus().post(new TopRightTitleEvent(pageTag));
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void onRightTitle2Click() {
        getEventBus().post(new TopRightTitle2Event());
    }

    public void onRightTitle3Click() {
        getEventBus().post(new TopRightTitle3Event());
    }
}