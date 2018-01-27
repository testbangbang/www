package com.onyx.jdread.personal.cloud.entity.jdbean;

/**
 * Created by li on 2018/1/26.
 */

public class GetPayQRCodeBean {
    public DataBean data;
    public int result_code;
    public String message;

    public static class DataBean {
        public int result_code;
        public String message;
        public String qr_code;
        public String trade_num;
    }
}
