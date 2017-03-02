package com.onyx.android.sdk.data.model;

import com.onyx.android.sdk.data.utils.QueryUtils;

import java.util.List;

/**
 * Created by suicheng on 2016/12/2.
 */

public class BaseQuery {
    public static final int MAX_LIMIT_COUNT = 1000;
    public static final int DEFAULT_LIMIT_COUNT = 30;

    public int offset;
    public int count = DEFAULT_LIMIT_COUNT;
    public String sortBy = QueryUtils.defaultSortBy();
    public Boolean sortOrder = QueryUtils.defaultSortOrder();

    public void resetOffset() {
        offset = 0;
    }

    public void next(final List list) {
        if (list == null) {
            offset = 0;
        } else {
            offset = list.size();
        }
    }

    public void resetLimitCount() {
        count = DEFAULT_LIMIT_COUNT;
    }

    public void maxLimitCount() {
        count = MAX_LIMIT_COUNT;
    }
}
