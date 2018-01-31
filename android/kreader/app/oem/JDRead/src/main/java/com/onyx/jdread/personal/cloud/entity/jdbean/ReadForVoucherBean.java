package com.onyx.jdread.personal.cloud.entity.jdbean;

/**
 * Created by li on 2018/1/30.
 */

public class ReadForVoucherBean {
    public DataBean data;
    public int result_code;
    public String message;

    public static class DataBean {
        public int code;
        public int voucher;
    }
}
