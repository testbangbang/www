package com.onyx.android.sun.cloud.bean;

import android.support.annotation.NonNull;

import com.onyx.android.sun.utils.TimeUtils;

/**
 * Created by li on 2017/10/10.
 */

public class ContentBean implements Comparable<ContentBean> {
    public int id;
    public String type;
    public String course;
    public String auth;
    public String title;
    public String deadline;
    public String status;

    @Override
    public int compareTo(@NonNull ContentBean contentBean) {
        if (contentBean.deadline == null || this.deadline == null) {
            return 0;
        }

        return TimeUtils.compareDate(contentBean.deadline, this.deadline);
    }
}
