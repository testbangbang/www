package com.onyx.android.sdk.data.model;

import com.onyx.android.sdk.data.utils.QueryUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 11/20/15.
 */
public class ProductQuery {

    public int offset;
    public int count = 30;
    public List<Integer> domains;
    public List<String> category;
    public String sortBy = QueryUtils.defaultSortBy();
    public Boolean sortOrder = QueryUtils.defaultSortOrder();
    public String key;
    public String value;
    public String ownerId;
    public long parentId;
    public int coverLimit = 10;

    public ProductQuery() {
        super();
        sortBy = QueryUtils.defaultSortBy();
        sortOrder = QueryUtils.defaultSortOrder();
    }

    public void resetCategory() {
        getCategory().clear();
    }

    public final List<String> getCategory() {
        if (category == null) {
            category = new ArrayList<String>();
        }
        return category;
    }

    public void addCategory(final String value) {
        getCategory().add(value);
    }

    public void useCategory(final String value) {
        resetCategory();
        addCategory(value);
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
