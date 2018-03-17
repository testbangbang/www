package com.onyx.jdread.shop.utils;

import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.cloud.entity.jdbean.GetVipGoodsListResultBean;

/**
 * Created by onyx on 2018/3/17.
 */

public class TextConversionUtils {
    public static String changeText(GetVipGoodsListResultBean.DataBean dataBean) {
        String date = "";
        if (dataBean != null) {
            date = dataBean.service_desc;
            switch (date) {
                case "一个月":
                    dataBean.service_desc = Constants.MONTHLY_VIP;
                    break;
                case "三个月":
                    dataBean.service_desc = Constants.QUARTER_VIP;
                    break;
                case "一年":
                    dataBean.service_desc = Constants.YEAR_VIP;
                    break;
                default:
                    break;
            }
        }
        return date;
    }
}
