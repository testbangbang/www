package com.onyx.jdread.shop.cloud.entity.jdbean;

/**
 * Created by huxiaomao on 17/3/28.
 */

public class BookDetailResultBean {
    public DetailBean data;
    public int resultCode;
    public String message;

    public static class DetailBean {
        public Object tag;
        public int ebook_id;
        public String name;
        public String author;
        public String image_url;
        public String large_image_url;
        public String info;
        public Object format;
        public String publisher;
        public String publish_time;
        public String edition;
        public int word_count;
        public float file_size;
        public float star;
        public boolean can_buy;
        public boolean can_read;
        public boolean can_try;
        public String try_url;
        public String price_message;
        public float price;
        public float jd_price;
        public String isbn;
        public int second_catid1;
        public String second_catid1_str;
        public int third_catid1;
        public String third_catid1_str;
        public String promotion;
        public String catalog;
        public boolean add_cart;
        public String downLoadUrl;
        public BookExtraInfoBean bookExtraInfoBean;
        public String key;
        public String random;
        public String order_id;
        public boolean isAlreadyBuy;
    }
}
