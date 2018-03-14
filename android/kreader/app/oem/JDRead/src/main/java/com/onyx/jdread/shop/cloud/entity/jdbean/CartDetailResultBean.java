package com.onyx.jdread.shop.cloud.entity.jdbean;

import java.util.List;

/**
 * Created by li on 2018/1/22.
 */

public class CartDetailResultBean {
    public DataBean data;
    public String result_code;
    public String message;

    public static class DataBean {
        public int re_price;
        public int origin_price;
        public String total_price;
        public List<SuitEntityListBean> suit_list;
    }
}
