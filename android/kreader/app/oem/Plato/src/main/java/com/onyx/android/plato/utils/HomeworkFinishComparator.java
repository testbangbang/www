package com.onyx.android.plato.utils;

import com.onyx.android.plato.cloud.bean.FinishContent;

import java.util.Comparator;

/**
 * Created by li on 2017/10/11.
 */

public class HomeworkFinishComparator implements Comparator<FinishContent> {
    @Override
    public int compare(FinishContent t0, FinishContent t1) {
        if (t0.submitTime == null || t1.submitTime == null) {
            return 0;
        }
        return TimeUtils.compareDate(t1.submitTime, t0.submitTime);
    }
}
