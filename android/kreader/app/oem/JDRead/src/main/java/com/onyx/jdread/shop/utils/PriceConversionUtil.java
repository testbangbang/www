package com.onyx.jdread.shop.utils;

import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ResManager;

/**
 * Created by lmb on 2018/3/20.
 */

public class PriceConversionUtil {
    private static int price;
    private static String A_MONTH = ResManager.getString(R.string.a_month);
    private static String THREE_MONTHS = ResManager.getString(R.string.three_months);
    private static String YEAR = ResManager.getString(R.string.year);

    public static int formatRMB(int yueDou) {
        price = yueDou / 100;
        return price;
    }

    public static String changeText(String desc) {
        if (desc != null) {
            if (desc.contains(A_MONTH)) {
                desc = ResManager.getString(R.string.monthly);
            } else if (desc.contains(THREE_MONTHS)) {
                desc = ResManager.getString(R.string.quarterly);
            } else if (desc.contains(YEAR)) {
                desc = ResManager.getString(R.string.annual);
            }
        }
        return desc;
    }
}
