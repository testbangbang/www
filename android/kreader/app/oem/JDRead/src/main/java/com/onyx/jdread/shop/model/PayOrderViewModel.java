package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;

import com.onyx.jdread.personal.cloud.entity.jdbean.UserInfo;
import com.onyx.jdread.shop.cloud.entity.jdbean.GetOrderInfoResultBean;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jackdeng on 2018/2/6.
 */

public class PayOrderViewModel extends BaseObservable {

    private EventBus eventBus;

    public PayOrderViewModel(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public final ObservableField<String> title = new ObservableField();
    private GetOrderInfoResultBean.DataBean orderInfo;
    private UserInfo userInfo;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public GetOrderInfoResultBean.DataBean getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(GetOrderInfoResultBean.DataBean orderInfo) {
        this.orderInfo = orderInfo;
    }

    public void onConfirmPayClick() {

    }
}
