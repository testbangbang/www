package com.onyx.jdread.personal.cloud.entity.jdbean;

import java.util.List;

/**
 * Created by li on 2018/1/25.
 */

public class GetRechargePackageBean {
    public int result_code;
    public String message;
    public List<DataBean> data;

    public static class DataBean {
        public int package_id;
        public double recharge_money;
        public int voucher_amount;
    }
}
