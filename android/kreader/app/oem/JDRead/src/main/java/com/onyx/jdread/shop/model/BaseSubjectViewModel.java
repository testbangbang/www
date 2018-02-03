package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;

import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelConfigResultBean;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jackdeng on 2017/12/11.
 */

public class BaseSubjectViewModel extends BaseObservable {

    private int subjectType;
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

    public int getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(int subjectType) {
        this.subjectType = subjectType;
    }
}