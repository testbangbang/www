package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;

import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelConfigResultBean;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by jackdeng on 2017/12/11.
 */

public class BannerViewModel extends BaseObservable {

    private List<BookModelConfigResultBean.DataBean.AdvBean> bannerList;
    private EventBus eventBus;

    public BannerViewModel(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public List<BookModelConfigResultBean.DataBean.AdvBean> getBannerList() {
        return bannerList;
    }

    public void setBannerList(List<BookModelConfigResultBean.DataBean.AdvBean> bannerList) {
        this.bannerList = bannerList;
        notifyChange();
    }
}