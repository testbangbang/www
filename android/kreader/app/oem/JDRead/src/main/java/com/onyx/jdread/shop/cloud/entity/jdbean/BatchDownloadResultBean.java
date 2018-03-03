package com.onyx.jdread.shop.cloud.entity.jdbean;

import android.databinding.BaseObservable;

import java.util.List;

/**
 * Created by jackdeng on 2018/3/3.
 */

public class BatchDownloadResultBean {

    public int result_code;
    public DataBean data;

    public static class DataBean {
        public boolean book_can_buy;
        public int auto_buy;
        public int voucher;
        public int yuedou;
        public List<ListBean> list;

        public static class ListBean extends BaseObservable {
            public int count;
            public String chapterCount;
            public String discount;
            public float jd_price;
            public float old_price;
            public List<String> ids;
        }
    }
}