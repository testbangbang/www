package com.onyx.jdread.personal.cloud.entity.jdbean;

/**
 * Created by li on 2018/2/26.
 */

public class CheckGiftBean {
    public DataBean data;
    public int result_code;
    public String message;

    public static class DataBean {
        public boolean gift;
    }
}
