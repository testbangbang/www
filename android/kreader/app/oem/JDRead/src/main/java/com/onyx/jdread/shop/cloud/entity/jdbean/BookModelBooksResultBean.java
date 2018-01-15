package com.onyx.jdread.shop.cloud.entity.jdbean;

import java.util.List;

/**
 * Created by jackdeng on 17-3-30.
 */

public class BookModelBooksResultBean {

    public DataBean data;
    public int result_code;
    public String message;

    public static class DataBean {

        public int total_page;
        public int total;
        public int page;
        public int page_size;
        public List<ResultBookBean> items;
    }
}
