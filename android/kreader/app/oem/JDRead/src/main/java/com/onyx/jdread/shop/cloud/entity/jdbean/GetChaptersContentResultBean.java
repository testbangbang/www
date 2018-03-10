package com.onyx.jdread.shop.cloud.entity.jdbean;

/**
 * Created by jackdeng on 2018/3/6.
 */

public class GetChaptersContentResultBean extends BaseResultBean{

    public DataBean data;

    public static class DataBean {
        public String id;
        public String content;
    }
}
