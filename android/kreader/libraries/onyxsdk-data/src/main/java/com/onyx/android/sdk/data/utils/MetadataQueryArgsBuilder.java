package com.onyx.android.sdk.data.utils;

import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryCriteria;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLCondition;
import com.raizlabs.android.dbflow.sql.language.property.IProperty;
import com.raizlabs.android.dbflow.sql.language.property.Property;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by suicheng on 2016/9/2.
 */
public class MetadataQueryArgsBuilder {

    public static QueryArgs bookListQuery(Set<String> fileTypes, OrderBy orderBy) {
        return QueryArgs.queryBy(orTypeCondition(fileTypes), orderBy);
    }

    public static QueryArgs bookListQuery(QueryCriteria queryCriteria, OrderBy orderBy) {
        return QueryArgs.queryBy(queryCriteriaCondition(queryCriteria), orderBy);
    }

    public static QueryArgs bookListQuery(ConditionGroup conditionGroup, OrderBy orderBy) {
        return QueryArgs.queryBy(conditionGroup, orderBy);
    }

    public static QueryArgs bookListQuery(ConditionGroup conditionGroup, List<OrderBy> orderByList) {
        return QueryArgs.queryBy(conditionGroup, orderByList);
    }

    public static QueryArgs newBookListQuery() {
        return QueryArgs.queryBy(newBookListCondition(), getOrderByCreateAt().descending());
    }

    public static QueryArgs finishReadQuery() {
        return QueryArgs.queryBy(finishReadCondition(), getOrderByUpdateAt().descending());
    }

    public static QueryArgs recentReadingQuery() {
        return QueryArgs.queryBy(recentReadingCondition(), getOrderByUpdateAt().descending());
    }

    public static QueryArgs recentAddQuery() {
        return QueryArgs.queryBy(recentAddCondition(), getOrderByUpdateAt().descending());
    }

    public static QueryArgs tagsFilterQuery(Set<String> tags, OrderBy orderBy) {
        return QueryArgs.queryBy(orTagsCondition(tags), getOrderByName()).appendOrderBy(orderBy);
    }

    public static ConditionGroup newBookListCondition() {
        return ConditionGroup.clause().and(Metadata_Table.lastAccess.isNull())
                .or(Metadata_Table.lastAccess.lessThanOrEq(new Date(0)));
    }

    public static ConditionGroup finishReadCondition() {
        return ConditionGroup.clause().and(Metadata_Table.lastAccess.isNotNull())
                .and(Metadata_Table.progress.isNotNull());
    }

    public static ConditionGroup recentReadingCondition() {
        return ConditionGroup.clause().and(Metadata_Table.progress.isNotNull())
                .and(Metadata_Table.lastAccess.isNotNull());
    }

    public static ConditionGroup recentAddCondition() {
        return ConditionGroup.clause().and(Metadata_Table.progress.isNull())
                .and(Metadata_Table.lastAccess.isNull());
    }

    public static ConditionGroup orTypeCondition(Set<String> fileTypes) {
        if (CollectionUtils.isNullOrEmpty(fileTypes)) {
            return ConditionGroup.clause();
        }
        List<SQLCondition> sqlConditions = new ArrayList<>();
        for (String type : fileTypes) {
            sqlConditions.add(Metadata_Table.type.eq(type));
        }
        return ConditionGroup.clause().orAll(sqlConditions);
    }

    public static ConditionGroup orTagsCondition(Set<String> tags) {
        if (CollectionUtils.isNullOrEmpty(tags)) {
            return ConditionGroup.clause();
        }
        List<SQLCondition> sqlConditions = new ArrayList<>();
        for (String tag : tags) {
            sqlConditions.add(Metadata_Table.tags.like(tag));
        }
        return ConditionGroup.clause().orAll(sqlConditions);
    }

    /**
     * default has orderBy name
     */
    public static List<OrderBy> getOrderByList() {
        List<OrderBy> orderByList = new ArrayList<>();
        orderByList.add(OrderBy.fromProperty(Metadata_Table.name).ascending());
        return orderByList;
    }

    public static OrderBy getOrderByUpdateAt() {
        return getOrderBy(Metadata_Table.updatedAt);
    }

    public static OrderBy getOrderByCreateAt() {
        return getOrderBy(Metadata_Table.createdAt);
    }

    public static OrderBy getOrderByName() {
        return getOrderBy(Metadata_Table.name).ascending();
    }

    public static OrderBy getOrderBy(IProperty property) {
        return OrderBy.fromProperty(property);
    }

    public static ConditionGroup queryCriteriaCondition(final QueryCriteria queryCriteria) {
        ConditionGroup group = ConditionGroup.clause();
        andWith(group, matchLikeSet(Metadata_Table.authors, queryCriteria.author));
        andWith(group, matchLikeSet(Metadata_Table.tags, queryCriteria.tags));
        andWith(group, matchLikeSet(Metadata_Table.series, queryCriteria.series));
        andWith(group, matchLikeSet(Metadata_Table.title, queryCriteria.title));
        andWith(group, matchLikeSet(Metadata_Table.type, queryCriteria.fileType));
        return group;
    }

    public static void andWith(final ConditionGroup parent, final ConditionGroup child) {
        if (parent != null && child != null) {
            parent.and(child);
        }
    }

    public static ConditionGroup matchLikeSet(final Property<String> property, final Set<String> set) {
        if (set == null || set.size() <= 0) {
            return null;
        }
        final ConditionGroup conditionGroup = ConditionGroup.clause();
        for (String string : set) {
            conditionGroup.or(property.like("%" + string + "%"));
        }
        return conditionGroup;
    }

    public static ConditionGroup matchEqualSet(final Property<String> property, final Set<String> set) {
        if (set == null || set.size() <= 0) {
            return null;
        }
        final ConditionGroup conditionGroup = ConditionGroup.clause();
        for (String string : set) {
            conditionGroup.or(property.eq(string));
        }
        return conditionGroup;
    }
}
