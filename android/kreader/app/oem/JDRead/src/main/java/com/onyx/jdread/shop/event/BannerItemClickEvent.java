package com.onyx.jdread.shop.event;

import com.onyx.jdread.shop.cloud.entity.jdbean.BookShopMainConfigResultBean;

/**
 * Created by jackdeng on 2018/1/10.
 */

public class BannerItemClickEvent {
    public BookShopMainConfigResultBean.DataBean.AdvBean advBean;

    public BannerItemClickEvent(BookShopMainConfigResultBean.DataBean.AdvBean advBean) {
        this.advBean = advBean;
    }
}
