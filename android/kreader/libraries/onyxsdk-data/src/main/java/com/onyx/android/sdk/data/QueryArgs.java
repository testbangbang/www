package com.onyx.android.sdk.data;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.property.IProperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by suicheng on 2016/9/2.
 */
public class QueryArgs {
    private static final String TAG = QueryArgs.class.getSimpleName();
    public static int CLOUD_FETCH_LIMIT = 50;

    public int offset = 0;
    public int limit = Integer.MAX_VALUE;

    @JSONField(serialize = false, deserialize = false)
    public ConditionGroup conditionGroup = ConditionGroup.clause();
    @JSONField(serialize = false, deserialize = false)
    public List<IProperty> propertyList = new ArrayList<>();
    @JSONField(serialize = false, deserialize = false)
    public List<OrderBy> orderByList = new ArrayList<>();
    public String libraryUniqueId = null;
    public BookFilter filter = BookFilter.ALL;

    public SortBy sortBy = SortBy.Name;
    public SortOrder order = SortOrder.Desc;
    public Set<String> fileType = new HashSet<>();
    public Set<String> author = new HashSet<>();
    public Set<String> title = new HashSet<>();
    public Set<String> tags = new HashSet<>();
    public Set<String> series = new HashSet<>();
    public Set<String> category = new HashSet<>();
    public String query;

    public static final String DEVICE_LIBRARY = "deviceLibrary";
    public static final String RECENT_READ = "recentRead";
    public static final String RECENT_ADDED = "recentAdded";

    public QueryArgs() {
    }

    public QueryArgs(SortBy sortBy, SortOrder order) {
        if (sortBy != null) {
            this.sortBy = sortBy;
        }
        if (order != null) {
            this.order = order;
        }
    }

    public static QueryArgs queryBy(final ConditionGroup conditionGroup,
                                    final OrderBy orderBy) {
        QueryArgs queryArgs = new QueryArgs();
        queryArgs.conditionGroup = conditionGroup;
        queryArgs.orderByList.add(orderBy);
        return queryArgs;
    }

    public static QueryArgs queryBy(final ConditionGroup conditionGroup,
                                    final List<OrderBy> orderByList) {
        QueryArgs queryArgs = new QueryArgs();
        queryArgs.conditionGroup = conditionGroup;
        queryArgs.orderByList.addAll(orderByList);
        return queryArgs;
    }

    public static QueryArgs queryBy(final ConditionGroup conditionGroup,
                                    final OrderBy orderBy,
                                    int offset, int limit) {
        QueryArgs queryArgs = queryBy(conditionGroup, orderBy);
        queryArgs.offset = offset;
        queryArgs.limit = limit;
        return queryArgs;
    }

    public static QueryArgs queryBy(final ConditionGroup conditionGroup,
                                    final List<OrderBy> orderByList,
                                    int offset, int limit) {
        QueryArgs queryArgs = queryBy(conditionGroup, orderByList);
        queryArgs.offset = offset;
        queryArgs.limit = limit;
        return queryArgs;
    }

    public static QueryArgs queryBy(final ConditionGroup conditionGroup) {
        QueryArgs queryArgs = new QueryArgs();
        queryArgs.conditionGroup = conditionGroup;
        return queryArgs;
    }

    public QueryArgs appendOrderBy(final OrderBy orderBy) {
        orderByList.add(orderBy);
        return this;
    }

    public QueryArgs andWith(ConditionGroup otherGroup) {
        conditionGroup.and(otherGroup);
        return this;
    }

    public QueryArgs orWith(ConditionGroup otherGroup) {
        conditionGroup.or(otherGroup);
        return this;
    }

    public QueryArgs appendFilter(BookFilter filter) {
        this.filter = filter;
        return this;
    }

    static public final QueryArgs fromQueryString(final String string) {
        QueryArgs args = null;
        try {
            args = JSON.parseObject(string, QueryArgs.class);
        } catch (Exception e) {
        } finally {
            return args;
        }
    }

    static public final String toQueryString(final QueryArgs args) {
        return JSON.toJSONString(args);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (!(object instanceof QueryArgs)) {
            return false;
        }
        QueryArgs queryCriteria = (QueryArgs) object;
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

    @JSONField(serialize = false, deserialize = false)
    public boolean isAllSetContentEmpty() {
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

    @JSONField(serialize = false, deserialize = false)
    public int getCloudFetchLimit() {
        return limit > QueryArgs.CLOUD_FETCH_LIMIT ? limit : QueryArgs.CLOUD_FETCH_LIMIT;
    }
}
