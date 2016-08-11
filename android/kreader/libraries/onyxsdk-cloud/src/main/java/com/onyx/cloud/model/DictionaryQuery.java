package com.onyx.cloud.model;

import com.onyx.cloud.utils.QueryUtils;

/**
 * Created by zhuzeng on 12/14/15.
 */
public class DictionaryQuery {

    public int offset;
    public int count;
    public String sortBy = QueryUtils.defaultSortBy();
    public Boolean sortOrder = QueryUtils.defaultSortOrder();
    public String sourceLanguage;
    public String targetLanguage;

    public DictionaryQuery() {
        super();
        sortBy = QueryUtils.defaultSortBy();
        sortOrder = QueryUtils.defaultSortOrder();
    }

}
