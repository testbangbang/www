package com.onyx.jdread.shop.cloud.entity.jdbean;

import java.util.List;

/**
 * Created by jackdeng on 2018/3/8.
 */

public class BuyChaptersResultBean extends BaseResultBean {

    public DataBean data;

    public static class DataBean {
        public int voucher;
        public int yuedou;
        public List<String> chapter_ids;
    }
}
