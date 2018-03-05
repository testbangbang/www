package com.onyx.jdread.shop.utils;

import android.app.Dialog;

import com.onyx.jdread.R;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.model.BaseSubjectViewModel;
import com.onyx.jdread.shop.model.SubjectType;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by jackdeng on 2018/1/18.
 */

public class ViewHelper {

    public static boolean isShowBookDetailView(BookDetailResultBean resultBean) {
        boolean show = false;
        if (resultBean != null && resultBean.data != null && Constants.RESULT_CODE_SUCCESS.equals(String.valueOf(resultBean.result_code))) {
            show = true;
        }
        return show;
    }

    public static String getYueDouPrice(float price) {
        if (price > 0) {
            return String.valueOf(new DecimalFormat("0").format(price));
        } else {
            return "0";
        }
    }

    public static String formatRMB(float price) {
        return String.valueOf(new DecimalFormat("0.00").format(price));
    }

    public static boolean isCanNowRead(BookDetailResultBean.DetailBean detailBean) {
        boolean canNowRead = false;
        if (detailBean != null) {
            canNowRead = detailBean.can_try;
        }
        return canNowRead;
    }

    public static int calculateTotalPages(List<BaseSubjectViewModel> subjectList, int recycleViewHeight) {
        int totalPage = 1;
        if (subjectList != null) {
            List<BaseSubjectViewModel> tempList = new ArrayList<>();
            tempList.addAll(subjectList);
            int itemSpace = ResManager.getInteger(R.integer.custom_recycle_view_space);
            int itemHeight = 0;
            for (int i = 0; i < tempList.size(); i++) {
                int subjectType = tempList.get(i).getSubjectType();
                switch (subjectType) {
                    case SubjectType.TYPE_TOP_FUNCTION:
                        itemHeight = itemHeight + Constants.SHOP_VIEW_TOP_FUNCTION_HEIGHT;
                        break;
                    case SubjectType.TYPE_BANNER:
                        itemHeight = itemHeight + Constants.SHOP_VIEW_BANNER_HEIGHT;
                        break;
                    case SubjectType.TYPE_TITLE:
                        itemHeight = itemHeight + Constants.SHOP_VIEW_TITLE_HEIGHT;
                        break;
                    case SubjectType.TYPE_COVER:
                        itemHeight = itemHeight + Constants.SHOP_VIEW_SUBJECT_HEIGHT;
                        break;
                    case SubjectType.TYPE_END:
                        itemHeight = itemHeight + Constants.SHOP_VIEW_END_VIEW_HEIGHT;
                        break;
                    case SubjectType.TYPE_VIP_USER:
                        itemHeight = itemHeight + Constants.SHOP_VIEW_VIP_INFO_VIEW_HEIGHT;
                        break;
                }
                itemHeight = itemHeight + itemSpace;
                if (itemHeight > recycleViewHeight) {
                    tempList.add(i, tempList.get(i));
                    itemHeight = 0;
                    totalPage++;
                }
            }
        }
        return totalPage;
    }

    public static String getPayByCashUrl(Map<String, String> params) {
        String url = CloudApiContext.getJDBooxBaseUrl() + CloudApiContext.ReadBean.PAY_BY_CASH;
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            StringBuffer sb = null;
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                if (sb == null) {
                    sb = new StringBuffer();
                    sb.append("?");
                } else {
                    sb.append("&");
                }
                sb.append(key);
                sb.append("=");
                sb.append(value);
            }
            url += sb.toString();
        }
        return url;
    }

    public static boolean dialogIsShowing(Dialog dialog) {
        boolean isShowing = false;
        if (dialog != null && dialog.isShowing()) {
            isShowing = true;
        }
        return isShowing;
    }
}
