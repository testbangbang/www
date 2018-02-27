package com.onyx.jdread.shop.cloud.entity.jdbean;

import java.util.List;

/**
 * Created by 12 on 2017/4/5.
 */

public class BookCommentsResultBean {
    public int result_code;
    public String message;
    public DataBean data;

    public static class DataBean {
        public int total;
        public int total_page;
        public List<CommentEntity> comments;
    }
}
