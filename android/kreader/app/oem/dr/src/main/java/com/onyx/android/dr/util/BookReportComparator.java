package com.onyx.android.dr.util;

import com.onyx.android.sdk.data.model.v2.CommentsBean;

import java.util.Comparator;

/**
 * Created by li on 2017/9/26.
 */

public class BookReportComparator implements Comparator<CommentsBean> {

    @Override
    public int compare(CommentsBean lhs, CommentsBean rhs) {
        int comparator = Integer.parseInt(lhs.left) - Integer.parseInt(rhs.left);
        return comparator;
    }
}
