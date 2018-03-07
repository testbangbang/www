package com.onyx.jdread.shop.cloud.entity.jdbean;

/**
 * Created by jackdeng on 2018/1/23.
 */

public class DownLoadWholeBookResultBean extends BaseResultBean{
    public DataBean data;

    public static class DataBean {
        public String random;
        public String key;
        public String content_url;
    }
}
