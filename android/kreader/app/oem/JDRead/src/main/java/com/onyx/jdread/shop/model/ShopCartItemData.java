package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;

import com.onyx.jdread.shop.cloud.entity.jdbean.BookCartItemBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.SimplifiedDetail;

/**
 * Created by li on 2018/1/5.
 */

public class ShopCartItemData extends BaseObservable {
    public SimplifiedDetail detail;
    public boolean checked;
    public boolean hasPromotion;
    public BookCartItemBean.CartResultBean.SuitEntityListBean.PromotionalEntityBean promotionalEntity;
    public double reAmount;
    public int shopNum;

    public SimplifiedDetail getDetail() {
        return detail;
    }

    public void setDetail(SimplifiedDetail detail) {
        this.detail = detail;
        notifyChange();
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        notifyChange();
    }

    public boolean isHasPromotion() {
        return hasPromotion;
    }

    public void setHasPromotion(boolean hasPromotion) {
        this.hasPromotion = hasPromotion;
        notifyChange();
    }

    public BookCartItemBean.CartResultBean.SuitEntityListBean.PromotionalEntityBean getPromotionalEntity() {
        return promotionalEntity;
    }

    public void setPromotionalEntity(BookCartItemBean.CartResultBean.SuitEntityListBean.PromotionalEntityBean promotionalEntity) {
        this.promotionalEntity = promotionalEntity;
        notifyChange();
    }

    public double getReAmount() {
        return reAmount;
    }

    public void setReAmount(double reAmount) {
        this.reAmount = reAmount;
        notifyChange();
    }

    public int getShopNum() {
        return shopNum;
    }

    public void setShopNum(int shopNum) {
        this.shopNum = shopNum;
        notifyChange();
    }
}
