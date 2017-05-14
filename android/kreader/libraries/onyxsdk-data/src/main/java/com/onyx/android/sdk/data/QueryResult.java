package com.onyx.android.sdk.data;

import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/5/6.
 */

public class QueryResult<T> {

    public List<T> list;
    public long count;
    public int fetchSource;

    public QueryResult<T> copy(int limit) {
        QueryResult<T> result = new QueryResult<>();
        result.count = count;
        result.fetchSource = fetchSource;
        result.list = new ArrayList<>();
        for (int i = 0; i < CollectionUtils.getSize(list) && i < limit; i++) {
            result.list.add(list.get(i));
        }
        return result;
    }

    public boolean isFetchFromCloud() {
        return fetchSource == Metadata.FetchSource.CLOUD;
    }
}
