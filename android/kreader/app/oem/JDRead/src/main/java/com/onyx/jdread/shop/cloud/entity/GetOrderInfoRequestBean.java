package com.onyx.jdread.shop.cloud.entity;

/**
 * Created by li on 2018/1/19.
 */

public class GetOrderInfoRequestBean {
    public String ebook_id;
    public int count = 1;

    public GetOrderInfoRequestBean(String ebook_id) {
        this.ebook_id = ebook_id;
    }
}
