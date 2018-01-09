package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;

import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelResultBean;
import com.onyx.jdread.shop.event.ViewAllClickEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jackdeng on 2017/12/11.
 */

public class SubjectViewModel extends BaseObservable{

    private EventBus eventBus;
    private BookModelResultBean modelBean;

    private BookModelResultBean modelBeanNext;

    private boolean showNextTitle;

    public EventBus getEventBus() {
        return eventBus;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public boolean isShowNextTitle() {
        return showNextTitle;
    }

    public void setShowNextTitle(boolean showNextTitle) {
        this.showNextTitle = showNextTitle;
        notifyChange();
    }

    public BookModelResultBean getModelBeanNext() {
        return modelBeanNext;
    }

    public void setModelBeanNext(BookModelResultBean modelBeanNext) {
        this.modelBeanNext = modelBeanNext;
        notifyChange();
    }

    public BookModelResultBean getModelBean() {
        return modelBean;
    }

    public void setModelBean(BookModelResultBean modelBean) {
        this.modelBean = modelBean;
        notifyChange();
    }

    public void onViewAllClick() {
        getEventBus().post(new ViewAllClickEvent(modelBean.moduleBookChild.fid, modelBean.moduleBookChild.showName));
    }

    public void onNextViewAllClick() {
        getEventBus().post(new ViewAllClickEvent(modelBeanNext.moduleBookChild.fid, modelBeanNext.moduleBookChild.showName));
    }
}