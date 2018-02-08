package com.onyx.jdread.shop.cloud.entity.jdbean;

import com.onyx.jdread.main.common.Constants;

import java.io.Serializable;

/**
 * Created by jackdeng on 2018/2/6.
 */

public class GetOrderInfoResultBean {

    public DataBean data;
    public int result_code;
    public String message;

    public static class DataBean implements Serializable {

        public String desc;
        public int pay_amount;
        public boolean need_recharge;
        public int need_recharge_count;
        public int yuedou_amount;
        public int voucher_amount;
        public String token;
    }

    public boolean isSucceed() {
        return result_code == Integer.valueOf(Constants.RESULT_CODE_SUCCESS);
    }
}
