package com.onyx.jdread.personal.cloud.entity.jdbean;

import java.util.List;

/**
 * Created by li on 2018/1/29.
 */

public class BoughtAndUnlimitedBean {
    public int result_code;
    public DataBean data;
    public String message;

    public static class DataBean {
        public int total;
        public int total_page;
        public int user_product_type;
        public List<BoughtAndUnlimitedItemBean> items;
    }
}
