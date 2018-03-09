package com.onyx.jdread.shop.cloud.entity;

import java.io.Serializable;

/**
 * Created by jackdeng on 2018/3/9.
 */

public class NetBookPayParamsBean implements Serializable {
    public long ebookId;
    public String bookName;
    public String start_chapter;
    public int count;
    public int voucher;
    public int yuedou;
    public int jd_price;
}
