package com.onyx.jdread.shop.cloud.entity.jdbean;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.Bitmap;

import com.facebook.common.references.CloseableReference;

/**
 * Created by huxiaomao on 17/3/28.
 */

public class BookDetailResultBean {
    public DetailBean data;
    public int result_code;
    public String message;

    public static class DetailBean extends BaseObservable{
        public final ObservableField<CloseableReference<Bitmap>> coverBitmap = new ObservableField<>();
        public final ObservableInt coverDefault = new ObservableInt();
        public Object tag;
        public int ebook_id;
        public String name;
        public String author;
        public String image_url;
        public String large_image_url;
        public String info;
        public String format;
        public String publisher;
        public String publish_time;
        public String edition;
        public int word_count;
        public float file_size;
        public float star;
        public boolean can_buy;//when can_buy is false,then alreadyBuy is true ;
        public boolean can_read;//vip read
        public boolean can_try;
        public String try_url;
        public String price_message;
        public float price;
        public float jd_price;
        public String isbn;
        public int second_catid1;
        public int second_cat_level;
        public String second_catid1_str;
        public int third_catid1;
        public int third_cat_level;
        public String third_catid1_str;
        public String promotion;
        public String catalog;
        public boolean add_cart;
        public int book_type;
        public int netStatus;
        public String modified;
        public String modified_str; //yyyy-MM-dd HH:mm:ss
        public boolean free;
        public String downLoadUrl;
        public BookExtraInfoBean bookExtraInfoBean;
        public String key;
        public String random;
        public String order_id;
        public boolean isAlreadyBuy;
        public int downLoadType;

        public void setAuthor(String author) {
            this.author = author;
            notifyChange();
        }
    }
}
