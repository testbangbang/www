package com.onyx.android.sdk.data;

import android.util.Log;

import com.alibaba.fastjson.annotation.JSONField;
import com.onyx.android.sdk.data.model.common.FetchPolicy;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.property.IProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/12/7.
 */
public class QueryBase implements Serializable {
    private static final String TAG = QueryBase.class.getSimpleName();

    public static int CLOUD_FETCH_LIMIT = 50;

    public int offset = 0;
    public int limit = Integer.MAX_VALUE;
    public SortBy sortBy = SortBy.Name;
    public SortOrder order = SortOrder.Desc;
    public String query;

    @JSONField(serialize = false, deserialize = false)
    public ConditionGroup conditionGroup = ConditionGroup.clause();
    @JSONField(serialize = false, deserialize = false)
    public List<IProperty> propertyList = new ArrayList<>();
    @JSONField(serialize = false, deserialize = false)
    public List<OrderBy> orderByList = new ArrayList<>();

    public
    @FetchPolicy.Type
    int fetchPolicy = FetchPolicy.MEM_CLOUD_DB;

    @JSONField(serialize = false, deserialize = false)
    public int getCloudFetchLimit() {
        return limit > CLOUD_FETCH_LIMIT ? limit : CLOUD_FETCH_LIMIT;
    }

    @JSONField(serialize = false, deserialize = false)
    public String getOrderByQuery() {
        if(CollectionUtils.isNullOrEmpty(orderByList)){
            return null;
        }
        String orderBy = "";
        for (OrderBy by : orderByList) {
            orderBy += by.getQuery();
        }
        return orderBy;
    }

    @JSONField(serialize = false, deserialize = false)
    public String getLimitOffsetQuery() {
        return " LIMIT " + limit + " OFFSET " + offset + " ";
    }

    @JSONField(serialize = false, deserialize = false)
    public String getOrderByQueryWithLimitOffset() {
        String orderByQuery = getOrderByQuery();
        String limitOffsetQuery = getLimitOffsetQuery();
        if(StringUtils.isNullOrEmpty(orderByQuery)) {
            Log.w(TAG, "NULL orderBy detected, offset and limit does not work.");
            return null;
        }
        return orderByQuery + limitOffsetQuery;
    }

    @JSONField(serialize = false, deserialize = false)
    public String[] getProjectionSet() {
        if (CollectionUtils.isNullOrEmpty(propertyList)) {
            return null;
        }
        String[] projection = new String[propertyList.size()];
        for (int i = 0; i < propertyList.size(); i++) {
            projection[i] = propertyList.get(i).getQuery();
        }
        return projection;
    }

    public void resetOffset() {
        this.offset = 0;
    }

    public void useMemCloudDbPolicy() {
        fetchPolicy = FetchPolicy.MEM_CLOUD_DB;
    }

    public void useCloudMemDbPolicy() {
        fetchPolicy = FetchPolicy.CLOUD_MEM_DB;
    }

    public void useCloudOnlyPolicy() {
        fetchPolicy = FetchPolicy.CLOUD_ONLY;
    }
}
