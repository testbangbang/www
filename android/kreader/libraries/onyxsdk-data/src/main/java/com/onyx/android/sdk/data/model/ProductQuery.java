package com.onyx.android.sdk.data.model;

import com.onyx.android.sdk.data.utils.QueryUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 11/20/15.
 */
public class ProductQuery extends BaseQuery{

    public int consumer = 0;
    public List<Integer> domains;
    public String category;
    public String key;
    public String value;
    public String ownerId;
    public int[] sourceTypes = new int[]{3};
    public long parentId;
    public int coverLimit = 10;

    public ProductQuery() {
        super();
        sortBy = QueryUtils.defaultSortBy();
        sortOrder = QueryUtils.defaultSortOrder();
    }

    public void resetCategory() {
        category = null;
    }

    public final String getCategory() {
        return category;
    }

    public void setCategory(final String value) {
        category = value;
    }

    public void resetKey() {
        key = null;
    }

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

}
