package com.onyx.jdread.shop.cloud.entity;

/**
 * Created by jackdeng on 2018/1/9.
 */

public class ShopMainConfigRequestBean extends BaseRequestInfo {
    private int cid;

    public ShopMainConfigRequestBean() {

    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }
}
