package com.onyx.jdread.shop.event;

import com.onyx.jdread.shop.cloud.entity.jdbean.GetVipGoodsListResultBean;

/**
 * Created by jackdeng on 2018/1/17.
 */

public class VipGoodItemClickEvent {
    public GetVipGoodsListResultBean.DataBean dataBean;

    public VipGoodItemClickEvent(GetVipGoodsListResultBean.DataBean dataBean) {
        this.dataBean = dataBean;
    }
}
