package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;

import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelConfigResultBean;
import com.onyx.jdread.shop.event.ViewAllClickEvent;
import com.onyx.jdread.shop.event.ViewAllNextClickEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jackdeng on 2017/12/11.
 */

public class SubjectViewModel extends BaseObservable {

    private EventBus eventBus;
    private BookModelConfigResultBean.DataBean.ModulesBean modelBean;

    public EventBus getEventBus() {
        return eventBus;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public BookModelConfigResultBean.DataBean.ModulesBean getModelBean() {
        return modelBean;
    }

    public void setModelBean(BookModelConfigResultBean.DataBean.ModulesBean modelBean) {
        this.modelBean = modelBean;
        notifyChange();
    }

    public void onViewAllClick() {
        getEventBus().post(new ViewAllClickEvent(modelBean));
    }

    public void onNextViewAllClick() {
        getEventBus().post(new ViewAllNextClickEvent(modelBean));
    }
}