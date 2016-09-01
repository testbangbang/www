package com.onyx.android.sdk.data;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.raizlabs.android.dbflow.sql.language.OrderBy;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhuzeng on 8/26/16.
 */
public class QueryCriteria {

    public Set<String> fileType = new HashSet<>();
    public Set<String> author = new HashSet<>();
    public Set<String> title = new HashSet<>();
    public Set<String> tags = new HashSet<>();
    public Set<String> series = new HashSet<>();

    public int offest = 0;
    public int limit = Integer.MAX_VALUE;
    public OrderBy orderBy;

    public QueryCriteria() {

    }

    static public final QueryCriteria fromQueryString(final String string) {
        QueryCriteria criteria = null;
        try {
            criteria = JSON.parseObject(string, QueryCriteria.class);
        } catch (Exception e) {
        } finally {
            return criteria;
        }
    }

    static public final String toQueryString(final QueryCriteria criteria) {
        return JSON.toJSONString(criteria);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!(object instanceof QueryCriteria)) {
            return false;
        }
        QueryCriteria queryCriteria = (QueryCriteria) object;
        if (!CollectionUtils.equals(queryCriteria.fileType, this.fileType)) {
            return false;
        }
        if (!CollectionUtils.equals(queryCriteria.title, this.title)) {
            return false;
        }
        if (!CollectionUtils.equals(queryCriteria.author, this.author)) {
            return false;
        }
        if (!CollectionUtils.equals(queryCriteria.tags, this.tags)) {
            return false;
        }
        if (!CollectionUtils.equals(queryCriteria.series, this.series)) {
            return false;
        }
        return true;
    }

    public boolean isAllContentEmpty() {
        if (!CollectionUtils.isNullOrEmpty(fileType)) {
            return false;
        }
        if (!CollectionUtils.isNullOrEmpty(title)) {
            return false;
        }
        if (!CollectionUtils.isNullOrEmpty(author)) {
            return false;
        }
        if (!CollectionUtils.isNullOrEmpty(tags)) {
            return false;
        }
        if (!CollectionUtils.isNullOrEmpty(series)) {
            return false;
        }
        return true;
    }
}