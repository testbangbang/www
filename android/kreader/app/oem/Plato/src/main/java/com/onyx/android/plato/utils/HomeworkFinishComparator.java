package com.onyx.android.plato.utils;

import com.onyx.android.plato.cloud.bean.ContentBean;

import java.util.Comparator;

/**
 * Created by li on 2017/10/11.
 */

public class HomeworkFinishComparator implements Comparator<ContentBean> {
    @Override
    public int compare(ContentBean t0, ContentBean t1) {
        if (t0.submitTime == null || t1.submitTime == null) {
            return 0;
        }
        return TimeUtils.compareDate(t1.submitTime, t0.submitTime);
    }
}
