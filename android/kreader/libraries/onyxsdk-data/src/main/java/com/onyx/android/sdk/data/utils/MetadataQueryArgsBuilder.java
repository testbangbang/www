package com.onyx.android.sdk.data.utils;

import com.onyx.android.sdk.data.BookFilter;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.onyx.android.sdk.data.model.MetadataCollection_Table;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLCondition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.language.Where;
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

    public static QueryArgs libraryAllBookQuery(String libraryUniqueId, SortBy sortBy, SortOrder sortOrder) {
        QueryArgs args = allBooksQuery(sortBy, sortOrder);
        args.libraryUniqueId = libraryUniqueId;
        return generateMetadataInQueryArgs(args);
    }

    public static QueryArgs libraryAllBookQuery(String libraryUniqueId, Set<String> fileTypes, SortBy sortBy, SortOrder sortOrder) {
        QueryArgs args = allBooksQuery(fileTypes, sortBy, sortOrder);
        args.libraryUniqueId = libraryUniqueId;
        return generateMetadataInQueryArgs(args);
    }

    public static QueryArgs libraryAllBookQuery(String libraryUniqueId, Set<String> fileTypes, OrderBy orderBy) {
        QueryArgs args = QueryArgs.queryBy(orTypeCondition(fileTypes), orderBy);
        args.libraryUniqueId = libraryUniqueId;
        return generateMetadataInQueryArgs(args);
    }

    public static QueryArgs libraryNewBookListQuery(String libraryUniqueId, SortBy sortBy, SortOrder sortOrder) {
        QueryArgs args = newBookListQuery(sortBy, sortOrder);
        args.libraryUniqueId = libraryUniqueId;
        return generateMetadataInQueryArgs(args);
    }

    public static QueryArgs libraryFinishReadQuery(String libraryUniqueId, SortBy sortBy, SortOrder sortOrder) {
        QueryArgs args = finishReadQuery(sortBy, sortOrder);
        args.libraryUniqueId = libraryUniqueId;
        return generateMetadataInQueryArgs(args);
    }

    public static QueryArgs libraryRecentReadingQuery(String libraryUniqueId, SortBy sortBy, SortOrder sortOrder) {
        QueryArgs args = recentReadingQuery(sortBy, sortOrder);
        args.libraryUniqueId = libraryUniqueId;
        return generateMetadataInQueryArgs(args);
    }

    public static QueryArgs libraryRecentAddQuery(String libraryUniqueId, SortBy sortBy, SortOrder sortOrder) {
        QueryArgs args = new QueryArgs(sortBy, sortOrder);
        args.libraryUniqueId = libraryUniqueId;
        args.conditionGroup = recentAddCondition();
        return generateMetadataInQueryArgs(args);
    }

    public static QueryArgs libraryTagsFilterQuery(String libraryUniqueId, Set<String> tags, SortBy sortBy, SortOrder order) {
        QueryArgs args = tagsFilterQuery(tags, sortBy, order);
        args.libraryUniqueId = libraryUniqueId;
        return generateMetadataInQueryArgs(args);
    }

    public static QueryArgs librarySearchQuery(String libraryUniqueId, String search, SortBy sortBy, SortOrder order) {
        QueryArgs args = searchQuery(search, sortBy, order);
        args.libraryUniqueId = libraryUniqueId;
        return generateMetadataInQueryArgs(args);
    }

    public static QueryArgs allBooksQuery(SortBy sortBy, SortOrder sortOrder) {
        QueryArgs args = new QueryArgs(sortBy, sortOrder).appendFilter(BookFilter.ALL);
        return generateQueryArgs(args);
    }

    public static QueryArgs allBooksQuery(Set<String> fileTypes, SortBy sortBy, SortOrder sortOrder) {
        QueryArgs args = new QueryArgs(sortBy, sortOrder).appendFilter(BookFilter.ALL);
        args.fileType = fileTypes;
        args.libraryUniqueId = BookFilter.ALL.toString();
        return generateQueryArgs(args);
    }

    public static QueryArgs allBooksQuery(Set<String> fileTypes, OrderBy orderBy) {
        return QueryArgs.queryBy(orTypeCondition(fileTypes), orderBy);
    }

    public static QueryArgs newBookListQuery(SortBy sortBy, SortOrder sortOrder) {
        QueryArgs args = new QueryArgs(sortBy, sortOrder).appendFilter(BookFilter.NEW_BOOKS);
        args.libraryUniqueId = BookFilter.NEW_BOOKS.toString();
        return generateQueryArgs(args);
    }

    public static QueryArgs finishReadQuery(SortBy sortBy, SortOrder sortOrder) {
        QueryArgs args = new QueryArgs(sortBy, sortOrder).appendFilter(BookFilter.READED);
        args.libraryUniqueId = BookFilter.READED.toString();
        return generateQueryArgs(args);
    }

    public static QueryArgs recentReadingQuery(SortBy sortBy, SortOrder sortOrder) {
        QueryArgs args = new QueryArgs(sortBy, sortOrder).appendFilter(BookFilter.READING);
        args.libraryUniqueId = BookFilter.READING.toString();
        return generateQueryArgs(args);
    }

    public static QueryArgs tagsFilterQuery(Set<String> tags, SortBy sortBy, SortOrder order) {
        QueryArgs args = new QueryArgs(sortBy, order).appendFilter(BookFilter.TAG);
        args.tags = tags;
        args.libraryUniqueId = BookFilter.TAG.toString();
        return generateQueryArgs(args);
    }

    public static QueryArgs searchQuery(String search, SortBy sortBy, SortOrder order) {
        QueryArgs args = new QueryArgs(sortBy, order).appendFilter(BookFilter.SEARCH);
        args.query = search;
        args.libraryUniqueId = BookFilter.SEARCH.toString();
        return generateQueryArgs(args);
    }

    public static QueryArgs recentAddQuery() {
        QueryArgs args = QueryArgs.queryBy(recentAddCondition(), getOrderByUpdateAt().descending());
        args.libraryUniqueId = BookFilter.RECENT_ADD.toString();
        return args;
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
                .and(Metadata_Table.lastAccess.isNotNull())
                .and(Metadata_Table.lastAccess.notEq(new Date(0)));
    }

    public static ConditionGroup recentAddCondition() {
        return ConditionGroup.clause().and(Metadata_Table.progress.isNull())
                .and(Metadata_Table.lastAccess.isNull());
    }

    public static ConditionGroup orTypeCondition(Set<String> fileTypes) {
        if (CollectionUtils.isNullOrEmpty(fileTypes)) {
            return ConditionGroup.clause();
        }
        ConditionGroup group = ConditionGroup.clause();
        List<SQLCondition> sqlConditions = new ArrayList<>();
        for (String type : fileTypes) {
            sqlConditions.add(Metadata_Table.type.eq(type));
        }
        return group.orAll(sqlConditions);
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

    public static ConditionGroup orSearchCondition(String search) {
        if (StringUtils.isNullOrEmpty(search)) {
            return ConditionGroup.clause();
        }
        return ConditionGroup.clause().or(matchLike(Metadata_Table.title, search))
                .or(matchLike(Metadata_Table.name, search)).or(matchLike(Metadata_Table.authors, search));
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

    public static void andWith(final ConditionGroup parent, final ConditionGroup child) {
        if (parent != null && child != null) {
            parent.and(child);
        }
    }

    public static Condition matchLike(final Property<String> property, String match) {
        if (StringUtils.isNullOrEmpty(match)) {
            return null;
        }
        return property.like("%" + match + "%");
    }

    public static ConditionGroup matchLikeSet(final Property<String> property, final Set<String> set) {
        if (set == null || set.size() <= 0) {
            return null;
        }
        final ConditionGroup conditionGroup = ConditionGroup.clause();
        for (String string : set) {
            Condition condition = matchLike(property, string);
            if (condition == null) {
                continue;
            }
            conditionGroup.or(condition);
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

    private static Condition getNullOrEqualCondition(Property<String> property, String compare) {
        return compare == null ? property.isNull() : property.eq(compare);
    }

    private static Condition getNotNullOrEqualCondition(Property<String> property, String compare) {
        return compare == null ? property.isNotNull() : property.eq(compare);
    }

    private static Condition.In inCondition(Property property, Where in, boolean isIn) {
        return isIn ? property.in(in) : property.notIn(in);
    }

    public static QueryArgs generateQueryArgs(QueryArgs args) {
        switch (args.filter) {
            case ALL:
                args.conditionGroup = orTypeCondition(args.fileType);
                break;
            case NEW_BOOKS:
                args.conditionGroup = newBookListCondition();
                break;
            case READING:
                args.conditionGroup = recentReadingCondition();
                break;
            case READED:
                args.conditionGroup = finishReadCondition();
                break;
            case TAG:
                args.conditionGroup = orTagsCondition(args.tags);
                break;
            case SEARCH:
                args.conditionGroup = orSearchCondition(args.query);
                break;
        }
        generateQueryArgsSortBy(args);
        generateCriteriaCondition(args);
        return args;
    }

    private static OrderBy ascDescOrder(OrderBy orderBy, boolean asc) {
        if (asc) {
            orderBy.ascending();
        } else {
            orderBy.descending();
        }
        return orderBy;
    }

    public static OrderBy generateOrderBy(SortBy sortBy, SortOrder order) {
        boolean asc = order == SortOrder.Asc;
        OrderBy orderBy = ascDescOrder(OrderBy.fromProperty(Metadata_Table.name), true);
        switch (sortBy) {
            case None:
            case Name:
                break;
            case FileType:
                orderBy = ascDescOrder(OrderBy.fromProperty(Metadata_Table.type), asc);
                break;
            case Size:
                orderBy = ascDescOrder(OrderBy.fromProperty(Metadata_Table.size), asc);
                break;
            case CreationTime:
                orderBy = ascDescOrder(OrderBy.fromProperty(Metadata_Table.lastModified), asc);
                break;
            case BookTitle:
                orderBy = ascDescOrder(OrderBy.fromProperty(Metadata_Table.title), asc);
                break;
            case Author:
                orderBy = ascDescOrder(OrderBy.fromProperty(Metadata_Table.authors), asc);
                break;
            case Publisher:
                orderBy = ascDescOrder(OrderBy.fromProperty(Metadata_Table.publisher), asc);
                break;
            case LastOpenTime:
                orderBy = ascDescOrder(OrderBy.fromProperty(Metadata_Table.lastAccess), asc);
                break;
            case RecentlyRead:
                orderBy = ascDescOrder(OrderBy.fromProperty(Metadata_Table.lastAccess), asc);
                break;
            case Total:
                orderBy = ascDescOrder(OrderBy.fromProperty(Metadata_Table.lastAccess), asc);
                break;
            case StartTime:
                orderBy = ascDescOrder(OrderBy.fromProperty(Metadata_Table.lastAccess), asc);
                break;
            case InstallTime:
                orderBy = ascDescOrder(OrderBy.fromProperty(Metadata_Table.lastAccess), asc);
                break;
        }
        return orderBy;
    }

    public static void generateQueryArgsSortBy(QueryArgs args) {
        args.orderByList.clear();
        args.orderByList.add(generateOrderBy(args.sortBy, args.order));
    }

    public static void generateCriteriaCondition(final QueryArgs args) {
        andWith(args.conditionGroup, matchLikeSet(Metadata_Table.authors, args.author));
        andWith(args.conditionGroup, matchLikeSet(Metadata_Table.tags, args.tags));
        andWith(args.conditionGroup, matchLikeSet(Metadata_Table.series, args.series));
        andWith(args.conditionGroup, matchLikeSet(Metadata_Table.title, args.title));
        andWith(args.conditionGroup, matchLikeSet(Metadata_Table.type, args.fileType));
    }

    public static QueryArgs generateMetadataInQueryArgs(final QueryArgs queryArgs) {
        Where<MetadataCollection> whereCollection = new Select(MetadataCollection_Table.documentUniqueId.withTable())
                .from(MetadataCollection.class)
                .where(getNotNullOrEqualCondition(MetadataCollection_Table.libraryUniqueId.withTable(), queryArgs.libraryUniqueId));
        Condition.In inCondition = inCondition(Metadata_Table.idString.withTable(), whereCollection, StringUtils.isNotBlank(queryArgs.libraryUniqueId));
        ConditionGroup group = ConditionGroup.clause().and(inCondition);
        if (queryArgs.conditionGroup.size() > 0) {
            queryArgs.conditionGroup = group.and(queryArgs.conditionGroup);
        } else {
            queryArgs.conditionGroup = group;
        }
        return queryArgs;
    }
}
