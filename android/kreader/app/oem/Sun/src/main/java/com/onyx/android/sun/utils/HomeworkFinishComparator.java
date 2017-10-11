package com.onyx.android.sun.utils;

import com.onyx.android.sun.cloud.bean.FinishContent;

import java.util.Comparator;

/**
 * Created by li on 2017/10/11.
 */

public class HomeworkFinishComparator implements Comparator<FinishContent> {
    @Override
    public int compare(FinishContent t0, FinishContent t1) {
        if (t0.correctTime == null || t1.correctTime == null) {
            return 0;
        }
        return TimeUtils.compareDate(t1.correctTime, t0.correctTime);
    }
}
