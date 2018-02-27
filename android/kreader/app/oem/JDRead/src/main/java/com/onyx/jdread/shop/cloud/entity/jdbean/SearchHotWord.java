package com.onyx.jdread.shop.cloud.entity.jdbean;

import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.List;

/**
 * Created by hehai on 18-1-18.
 */

public class SearchHotWord {

    public DataBean data;
    public int result_code;
    public String message;

    public static class DataBean {
        public String defaultKeyWord;
        public List<String> keyWord;
    }

    public boolean isKeyWordEmpty() {
        return data == null || CollectionUtils.isNullOrEmpty(data.keyWord);
    }
}
