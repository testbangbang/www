package com.onyx.jdread.shop.cloud.entity.jdbean;

import android.databinding.BaseObservable;

import com.onyx.jdread.main.common.Constants;

import java.util.List;

/**
 * Created by jackdeng on 2018/3/3.
 */

public class BatchDownloadResultBean {

    public int result_code;
    public String message;
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
            public int jd_price;
            public int old_price;
            public List<String> ids;
        }
    }

    public boolean isSucceed() {
        return result_code == Integer.valueOf(Constants.RESULT_CODE_SUCCESS);
    }
}