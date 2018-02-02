package com.onyx.jdread.shop.cloud.entity.jdbean;

import java.util.List;

/**
 * Created by jackdeng on 2018/2/2.
 */

public class GetVipGoodsListResultBean {

    public int result_code;
    public String message;
    public List<DataBean> data;

    public static class DataBean {
        public String service_desc;
        public int sku_id;
        public int seq;
        public int promotion_price;
        public boolean can_buy;
    }
}
