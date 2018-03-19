package com.onyx.jdread.shop.cloud.entity.jdbean;

import java.util.List;

/**
 * Created by jackdeng on 2018/3/6.
 */

public class GetChaptersContentResultBean extends BaseResultBean{
    public List<DataBean> data;

    public static class DataBean {
        public String time;
        public String pin;
        public String id;
        public String content;
        public int can_try;
    }
}
