package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;

import com.onyx.jdread.shop.cloud.entity.jdbean.GetVipGoodsListResultBean;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by jackdeng on 2018/2/2.
 */

public class BuyReadVipModel extends BaseObservable {

    private EventBus eventBus;
    private List<GetVipGoodsListResultBean.DataBean> goodsList;
    private TitleBarViewModel titleBarViewModel;
    private VipUserInfoViewModel vipUserInfoViewModel;

    public BuyReadVipModel(EventBus eventBus) {
        this.eventBus = eventBus;
        titleBarViewModel = new TitleBarViewModel();
        titleBarViewModel.setEventBus(eventBus);
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public List<GetVipGoodsListResultBean.DataBean> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<GetVipGoodsListResultBean.DataBean> goodsList) {
        this.goodsList = goodsList;
        notifyChange();
    }

    public TitleBarViewModel getTitleBarViewModel() {
        return titleBarViewModel;
    }

    public VipUserInfoViewModel getVipUserInfoViewModel() {
        return vipUserInfoViewModel;
    }

    public void setVipUserInfoViewModel(VipUserInfoViewModel vipUserInfoViewModel) {
        this.vipUserInfoViewModel = vipUserInfoViewModel;
        notifyChange();
    }
}
