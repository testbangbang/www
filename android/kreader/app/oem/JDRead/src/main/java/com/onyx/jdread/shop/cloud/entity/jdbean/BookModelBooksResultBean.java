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
        public List<ItemsBean> items;

        public static class ItemsBean {

            public int ebook_id;
            public String name;
            public String author;
            public String image_url;
            public String large_image_url;
            public String info;
        }
    }
}
