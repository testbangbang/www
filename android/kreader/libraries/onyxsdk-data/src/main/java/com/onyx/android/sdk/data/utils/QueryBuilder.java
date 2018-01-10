package com.onyx.android.sdk.data.utils;

import com.onyx.android.sdk.data.BookFilter;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.onyx.android.sdk.data.model.MetadataCollection_Table;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.sql.language.Operator;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLOperator;
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
public class QueryBuilder {

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

    public static QueryArgs libraryBookListNewQuery(String libraryUniqueId, SortBy sortBy, SortOrder sortOrder) {
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
        return generateQueryArgs(args);
    }

    public static QueryArgs allBooksQuery(Set<String> fileTypes, OrderBy orderBy) {
        return QueryArgs.queryBy(orTypeCondition(fileTypes), orderBy);
    }

    public static QueryArgs newBookListQuery(SortBy sortBy, SortOrder sortOrder) {
        QueryArgs args = new QueryArgs(sortBy, sortOrder).appendFilter(BookFilter.NEW);
        return generateQueryArgs(args);
    }

    public static QueryArgs finishReadQuery(SortBy sortBy, SortOrder sortOrder) {
        QueryArgs args = new QueryArgs(sortBy, sortOrder).appendFilter(BookFilter.FINISHED);
        return generateQueryArgs(args);
    }

    public static QueryArgs recentReadingQuery(SortBy sortBy, SortOrder sortOrder) {
        QueryArgs args = new QueryArgs(sortBy, sortOrder).appendFilter(BookFilter.READING);
        return generateQueryArgs(args);
    }

    public static QueryArgs tagsFilterQuery(Set<String> tags, SortBy sortBy, SortOrder order) {
        QueryArgs args = new QueryArgs(sortBy, order).appendFilter(BookFilter.TAG);
        args.tags = tags;
        return generateQueryArgs(args);
    }

    public static QueryArgs searchQuery(String search, SortBy sortBy, SortOrder order) {
        QueryArgs args = new QueryArgs(sortBy, order).appendFilter(BookFilter.SEARCH);
        args.query = search;
        return generateQueryArgs(args);
    }

    public static QueryArgs recentAddQuery() {
        QueryArgs args = QueryArgs.queryBy(recentAddCondition(), getOrderByUpdateAt().descending());
        return args;
    }

    public static Property<Integer> getMetadataReadingStatusProperty() {
        return Metadata_Table.readingStatus;
    }

    public static Property<String> getMetadataTypeProperty() {
        return Metadata_Table.type;
    }

    public static Property<String> getMetadataTitleProperty() {
        return Metadata_Table.title;
    }

    public static Property<String> getMetadataNameProperty() {
        return Metadata_Table.name;
    }

    public static Property<String> getMetadataAuthorsProperty() {
        return Metadata_Table.authors;
    }

    public static Property<String> getMetadataPublisherProperty() {
        return Metadata_Table.publisher;
    }

    public static Property<String> getMetadataTagsProperty() {
        return Metadata_Table.tags;
    }

    public static Property<String> getMetadataSeriesProperty() {
        return Metadata_Table.series;
    }

    public static Property<String> getMetadataIdStringProperty() {
        return Metadata_Table.idString;
    }

    public static Property<Long> getMetadataSizeProperty() {
        return Metadata_Table.size;
    }

    public static Property<Date> getMetadataCreatedAtProperty() {
        return Metadata_Table.createdAt;
    }

    public static Property<Date> getMetadataLastAccessProperty() {
        return Metadata_Table.lastAccess;
    }

    public static Property<Integer> getMetadataOrdinalProperty() {
        return Metadata_Table.ordinal;
    }

    public static Property<String> getMetadataCollectionDocIdProperty() {
        return MetadataCollection_Table.documentUniqueId;
    }

    public static Property<String> getMetadataCollectionLibraryIdProperty() {
        return MetadataCollection_Table.libraryUniqueId;
    }

    public static OperatorGroup newBookListCondition() {
        return OperatorGroup.clause().and(getMetadataReadingStatusProperty().eq(Metadata.ReadingStatus.NEW));
    }

    public static OperatorGroup finishReadCondition() {
        return OperatorGroup.clause().and(getMetadataReadingStatusProperty().eq(Metadata.ReadingStatus.FINISHED));
    }

    public static OperatorGroup recentReadingCondition() {
        return OperatorGroup.clause().and(getMetadataReadingStatusProperty().eq(Metadata.ReadingStatus.READING));
    }

    public static OperatorGroup recentAddCondition() {
        return OperatorGroup.clause().and(getMetadataReadingStatusProperty().eq(Metadata.ReadingStatus.NEW));
    }

    public static OperatorGroup orTypeCondition(Set<String> fileTypes) {
        if (CollectionUtils.isNullOrEmpty(fileTypes)) {
            return OperatorGroup.clause();
        }
        OperatorGroup group = OperatorGroup.clause();
        List<SQLOperator> sqlConditions = new ArrayList<>();
        for (String type : fileTypes) {
            sqlConditions.add(getMetadataTypeProperty().eq(type));
        }
        return group.orAll(sqlConditions);
    }

    public static OperatorGroup orTagsCondition(Set<String> tags) {
        if (CollectionUtils.isNullOrEmpty(tags)) {
            return OperatorGroup.clause();
        }
        List<SQLOperator> sqlConditions = new ArrayList<>();
        for (String tag : tags) {
            sqlConditions.add(matchLike(getMetadataTagsProperty(), tag));
        }
        return OperatorGroup.clause().orAll(sqlConditions);
    }

    public static OperatorGroup orSearchCondition(String search) {
        if (StringUtils.isNullOrEmpty(search)) {
            return OperatorGroup.clause();
        }
        return OperatorGroup.clause().or(matchLike(getMetadataTitleProperty(), search))
                .or(matchLike(getMetadataNameProperty(), search)).or(matchLike(getMetadataAuthorsProperty(), search));
    }

    /**
     * default has orderBy name
     */
    public static List<OrderBy> getOrderByList() {
        List<OrderBy> orderByList = new ArrayList<>();
        orderByList.add(OrderBy.fromProperty(getMetadataNameProperty()).ascending());
        return orderByList;
    }

    public static OrderBy getOrderByUpdateAt() {
        return getOrderBy(getMetadataCreatedAtProperty());
    }

    public static OrderBy getOrderByCreateAt() {
        return getOrderBy(getMetadataCreatedAtProperty());
    }

    public static OrderBy getOrderByName() {
        return getOrderBy(getMetadataNameProperty()).ascending();
    }

    public static OrderBy getOrderBy(IProperty property) {
        return OrderBy.fromProperty(property);
    }

    public static void andWith(final OperatorGroup parent, final OperatorGroup child) {
        if (parent != null && child != null) {
            parent.and(child);
        }
    }

    public static SQLOperator matchNotLike(final Property<String> property, final String value) {
        if (property == null || StringUtils.isNullOrEmpty(value)) {
            return null;
        }
        return new SQLOperator() {
            private String separator;
            @Override
            public void appendConditionToQuery(com.raizlabs.android.dbflow.sql.QueryBuilder queryBuilder) {
                queryBuilder.append(columnName()).append(operation());
                queryBuilder.append(Operator.convertValueToString(value(), true));
            }

            @Override
            public String columnName() {
                return property.getNameAlias().name();
            }

            @Override
            public String separator() {
                return separator;
            }

            @Override
            public SQLOperator separator(String separator) {
                this.separator = separator;
                return this;
            }

            @Override
            public boolean hasSeparator() {
                return StringUtils.isNotBlank(separator);
            }

            @Override
            public String operation() {
                return String.format(" %1s ", "NOT " + Operator.Operation.LIKE);
            }

            @Override
            public Object value() {
                return "%" + value + "%";
            }
        };
    }

    public static Operator matchLike(final Property<String> property, String match) {
        if (StringUtils.isNullOrEmpty(match)) {
            return null;
        }
        return property.like("%" + match + "%");
    }

    public static OperatorGroup matchLikeSet(final Property<String> property, final Set<String> set) {
        if (set == null || set.size() <= 0) {
            return null;
        }
        final OperatorGroup conditionGroup = OperatorGroup.clause();
        for (String string : set) {
            Operator condition = matchLike(property, string);
            if (condition == null) {
                continue;
            }
            conditionGroup.or(condition);
        }
        return conditionGroup;
    }

    public static OperatorGroup matchEqualSet(final Property<String> property, final Set<String> set) {
        if (set == null || set.size() <= 0) {
            return null;
        }
        final OperatorGroup conditionGroup = OperatorGroup.clause();
        for (String string : set) {
            conditionGroup.or(property.eq(string));
        }
        return conditionGroup;
    }

    private static Operator getNullOrEqualCondition(Property<String> property, String compare) {
        return compare == null ? property.isNull() : property.eq(compare);
    }

    private static Operator getNotNullOrEqualCondition(Property<String> property, String value) {
        return value == null ? property.isNotNull() : property.eq(value);
    }

    private static Operator.In inCondition(Property property, Where in, boolean isIn) {
        return isIn ? property.in(in) : property.notIn(in);
    }

    public static QueryArgs generateQueryArgs(QueryArgs args) {
        switch (args.filter) {
            case ALL:
                args.conditionGroup = orTypeCondition(args.fileType);
                break;
            case NEW:
                args.conditionGroup = newBookListCondition();
                break;
            case READING:
                args.conditionGroup = recentReadingCondition();
                break;
            case FINISHED:
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

    public static OrderBy ascDescOrder(OrderBy orderBy, boolean asc) {
        if (asc) {
            orderBy.ascending();
        } else {
            orderBy.descending();
        }
        return orderBy;
    }

    public static Property getPropertyFromSortBy(SortBy sortBy) {
        Property property = getMetadataNameProperty();
        switch (sortBy) {
            case None:
            case Name:
                break;
            case FileType:
                property = getMetadataTypeProperty();
                break;
            case Size:
                property = getMetadataSizeProperty();
                break;
            case CreationTime:
                property = getMetadataCreatedAtProperty();
                break;
            case BookTitle:
                property = getMetadataTitleProperty();
                break;
            case Author:
                property = getMetadataAuthorsProperty();
                break;
            case Publisher:
                property = getMetadataPublisherProperty();
                break;
            case LastOpenTime:
                property = getMetadataLastAccessProperty();
                break;
            case RecentlyRead:
                property = getMetadataLastAccessProperty();
                break;
            case Total:
                property = getMetadataLastAccessProperty();
                break;
            case StartTime:
                property = getMetadataLastAccessProperty();
                break;
            case InstallTime:
                property = getMetadataLastAccessProperty();
                break;
            case Ordinal:
                property = getMetadataOrdinalProperty();
                break;
        }
        return property;
    }

    public static OrderBy generateOrderBy(SortBy sortBy, SortOrder order) {
        boolean asc = order == SortOrder.Asc;
        return ascDescOrder(OrderBy.fromProperty(getPropertyFromSortBy(sortBy)), asc);
    }

    public static void generateQueryArgsSortBy(QueryArgs args) {
        args.orderByList.clear();
        args.orderByList.add(generateOrderBy(args.sortBy, args.order));
    }

    public static void generateCriteriaCondition(final QueryArgs args) {
        andWith(args.conditionGroup, matchLikeSet(getMetadataAuthorsProperty(), args.author));
        andWith(args.conditionGroup, matchLikeSet(getMetadataTagsProperty(), args.tags));
        andWith(args.conditionGroup, matchLikeSet(getMetadataSeriesProperty(), args.series));
        andWith(args.conditionGroup, matchLikeSet(getMetadataTitleProperty(), args.title));
        andWith(args.conditionGroup, matchLikeSet(getMetadataTypeProperty(), args.fileType));
    }

    public static QueryArgs generateMetadataInQueryArgs(final QueryArgs queryArgs) {
        Where<MetadataCollection> whereCollection = new Select(getMetadataCollectionDocIdProperty().withTable())
                .from(MetadataCollection.class)
                .where(getNotNullOrEqualCondition(getMetadataCollectionLibraryIdProperty().withTable(), queryArgs.libraryUniqueId));
        Operator.In inCondition = inCondition(getMetadataIdStringProperty().withTable(), whereCollection, StringUtils.isNotBlank(queryArgs.libraryUniqueId));
        OperatorGroup group = OperatorGroup.clause().and(inCondition);
        if (queryArgs.conditionGroup.size() > 0) {
            queryArgs.conditionGroup = group.and(queryArgs.conditionGroup);
        } else {
            queryArgs.conditionGroup = group;
        }
        return queryArgs;
    }
}
