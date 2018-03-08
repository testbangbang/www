package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2018/1/5.
 */

public class ShopCartModel extends BaseObservable {
    private List<ShopCartItemData> datas;
    private String originalPrice;
    private String cashBack;
    private String totalAmount;
    private String size;
    private boolean selectedAll=false;
    private String pageSize;
    public boolean settlementEnable;
    public boolean checkAllEnable;

    public List<ShopCartItemData> getDatas() {
        return datas;
    }

    public void setDatas(List<ShopCartItemData> datas) {
        this.datas = datas;
        notifyChange();
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
        notifyChange();
    }

    public String getCashBack() {
        return cashBack;
    }

    public void setCashBack(String cashBack) {
        this.cashBack = cashBack;
        notifyChange();
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
        notifyChange();
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
        notifyChange();
    }

    public boolean isSelectedAll() {
        return selectedAll;
    }

    public void setSelectedAll(boolean selectedAll) {
        this.selectedAll = selectedAll;
        notifyChange();
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
        notifyChange();
    }

    public String getPageSize() {
        return pageSize;
    }

    public boolean isSettlementEnable() {
        return settlementEnable;
    }

    public void setSettlementEnable(boolean settlementEnable) {
        this.settlementEnable = settlementEnable;
        notifyChange();
    }

    public boolean isCheckAllEnable() {
        return checkAllEnable;
    }

    public void setCheckAllEnable(boolean checkAllEnable) {
        this.checkAllEnable = checkAllEnable;
        notifyChange();
    }
}
