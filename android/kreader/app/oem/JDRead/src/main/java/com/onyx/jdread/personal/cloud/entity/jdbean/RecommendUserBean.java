package com.onyx.jdread.personal.cloud.entity.jdbean;

import java.util.List;

/**
 * Created by li on 2018/1/25.
 */

public class RecommendUserBean {
    public DataBean data;
    public int result_code;
    public String message;

    public static class DataBean {
        public int page;
        public int page_size;
        public int total;
        public int total_page;
        public List<RecommendItemBean> items;
    }
}
