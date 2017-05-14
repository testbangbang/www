package com.onyx.android.sdk.data;

import java.util.List;

/**
 * Created by suicheng on 2017/5/6.
 */

public class QueryResult<T> {

    public List<T> list;
    public long count;
    public int fetchSource;

    public QueryResult<T> copy() {
        QueryResult<T> result = new QueryResult<>();
        result.count = count;
        result.fetchSource = fetchSource;
        result.list = list;
        return result;
    }
}
