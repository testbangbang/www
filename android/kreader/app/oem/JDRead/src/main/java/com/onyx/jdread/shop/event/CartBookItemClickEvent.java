package com.onyx.jdread.shop.event;

import com.onyx.jdread.shop.model.ShopCartItemData;

/**
 * Created by tangzhijie on 2018/03/06.
 */

public class CartBookItemClickEvent {

    private ShopCartItemData bookBean;

    public CartBookItemClickEvent(ShopCartItemData bookBean) {
        this.bookBean = bookBean;
    }

    public ShopCartItemData getBookBean() {
        return bookBean;
    }

    public void setBookBean(ShopCartItemData bookBean) {
        this.bookBean = bookBean;
    }
}