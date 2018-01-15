package com.onyx.jdread.shop.event;

import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelConfigResultBean;

/**
 * Created by jackdeng on 2018/1/10.
 */

public class BannerItemClickEvent {
    public BookModelConfigResultBean.DataBean.AdvBean advBean;

    public BannerItemClickEvent(BookModelConfigResultBean.DataBean.AdvBean advBean) {
        this.advBean = advBean;
    }
}
