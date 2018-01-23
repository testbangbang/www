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
        public double cashback;
        public double original_price;
        public String total_costcontent;
        public String total_costcontent2;
        public int total_num;
        public List<SignalProductListBean> signal_product_list;
        public List<SuitEntityListBean> suit_entity_list;
    }
}
