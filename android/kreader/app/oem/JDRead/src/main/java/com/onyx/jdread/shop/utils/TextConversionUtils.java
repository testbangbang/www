package com.onyx.jdread.shop.utils;

import com.onyx.jdread.R;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.shop.cloud.entity.jdbean.GetVipGoodsListResultBean;

/**
 * Created by lmb on 2018/3/17.
 */

public class TextConversionUtils {
    private static String A_MONTH = ResManager.getString(R.string.a_month);
    private static String THREE_MONTHS = ResManager.getString(R.string.three_months);
    private static String ONE_YEAR = ResManager.getString(R.string.one_year);

    public static String changeText(String date) {
        if (date != null) {
            if (A_MONTH.equals(date)) {
                date = ResManager.getString(R.string.monthly_vip);
            } else if (THREE_MONTHS.equals(date)) {
                date = ResManager.getString(R.string.quarter_vip);
            } else if (ONE_YEAR.equals(date)) {
                date = ResManager.getString(R.string.year_vip);
            }
        }
        return date;
    }
}
