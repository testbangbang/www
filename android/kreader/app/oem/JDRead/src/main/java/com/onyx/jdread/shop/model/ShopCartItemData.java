package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;
import android.support.annotation.NonNull;

import com.onyx.jdread.shop.cloud.entity.jdbean.BookCartItemBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.PromotionalEntityBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.SimplifiedDetail;

import java.lang.reflect.Field;

/**
 * Created by li on 2018/1/5.
 */

public class ShopCartItemData extends BaseObservable implements Comparable<ShopCartItemData>{
    public SimplifiedDetail detail;
    public boolean checked;
    public boolean hasPromotion;
    public PromotionalEntityBean promotionalEntity;
    public double reAmount;
    public int shopNum;
    public int sort;

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

    public PromotionalEntityBean getPromotionalEntity() {
        return promotionalEntity;
    }

    public void setPromotionalEntity(PromotionalEntityBean promotionalEntity) {
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

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    @Override
    public int compareTo(@NonNull ShopCartItemData o) {
        return this.sort - o.getSort();
    }
}
