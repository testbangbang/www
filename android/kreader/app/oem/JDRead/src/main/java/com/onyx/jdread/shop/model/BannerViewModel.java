package com.onyx.jdread.shop.model;

import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelConfigResultBean;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by jackdeng on 2017/12/11.
 */

public class BannerViewModel extends BaseSubjectViewModel {

    private List<BookModelConfigResultBean.DataBean.AdvBean> bannerList;

    public BannerViewModel(EventBus eventBus) {
        setEventBus(eventBus);
        setSubjectType(SubjectType.TYPE_BANNER);
    }

    public List<BookModelConfigResultBean.DataBean.AdvBean> getBannerList() {
        return bannerList;
    }

    public void setBannerList(List<BookModelConfigResultBean.DataBean.AdvBean> bannerList) {
        this.bannerList = bannerList;
    }
}