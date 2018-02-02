package com.onyx.jdread.shop.utils;

import com.onyx.jdread.R;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.model.BaseSubjectViewModel;
import com.onyx.jdread.shop.model.SubjectType;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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
}
