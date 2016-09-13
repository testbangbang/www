package com.onyx.android.sdk.data;

import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.OrderBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by suicheng on 2016/9/2.
 */
public class QueryArgs {

    public int limit = Integer.MAX_VALUE;
    public int offset = 0;
    public ConditionGroup conditionGroup = ConditionGroup.clause();
    public List<OrderBy> orderByList = new ArrayList<>();
    public String parentId = null;
    public BookFilter filter = BookFilter.ALL;
    public SortBy sortBy = SortBy.Name;
    public SortOrder order = SortOrder.Desc;
    public Set<String> contentType;
    public Set<String> tags;
    public String query;

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
}
