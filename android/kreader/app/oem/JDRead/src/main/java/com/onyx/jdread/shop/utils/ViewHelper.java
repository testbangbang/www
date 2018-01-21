package com.onyx.jdread.shop.utils;

import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;

import java.text.DecimalFormat;

/**
 * Created by jackdeng on 2018/1/18.
 */

public class ViewHelper {

    public static boolean isShowBookDetailView(BookDetailResultBean resultBean){
        boolean show = false;
        if (resultBean != null && resultBean.data != null && Constants.RESULT_CODE_SUCCESS.equals(String.valueOf(resultBean.result_code))) {
            show = true;
        }
        return show;
    }

    public static String getYueDouPrice(float price) {
        return String.valueOf(new DecimalFormat("0").format(price * 100));
    }

    public static boolean isCanNowRead(BookDetailResultBean.DetailBean detailBean){
        boolean canNowRead = false;
        if (detailBean != null) {
            canNowRead = detailBean.can_try;
        }
        return canNowRead;
    }
}
