package com.onyx.android.sdk.data.utils;

import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryArgs.*;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.raizlabs.android.dbflow.sql.language.OrderBy;

/**
 * Created by suicheng on 2016/9/6.
 */
public class QueryArgsUtils {

    public static void generateQueryArgsCondition(QueryArgs args) {
        switch (args.filter) {
            case ALL:
                args.conditionGroup = MetadataQueryArgsBuilder.orTypeCondition(args.contentType);
                break;
            case NEW_BOOKS:
                args.conditionGroup = MetadataQueryArgsBuilder.newBookListCondition();
                break;
            case READING:
                args.conditionGroup = MetadataQueryArgsBuilder.recentReadingCondition();
                break;
            case READED:
                args.conditionGroup = MetadataQueryArgsBuilder.finishReadCondition();
                break;
            case TAG:
                args.conditionGroup = MetadataQueryArgsBuilder.orTagsCondition(args.tags);
                break;
            case SEARCH:
                args.conditionGroup = MetadataQueryArgsBuilder.orSearchCondition(args.query);
                break;
        }
        MetadataQueryArgsBuilder.andMetadataParentId(args, args.parentId);
        generateQueryArgsSortBy(args);
    }

    private static OrderBy ascDescOrder(OrderBy orderBy, boolean asc) {
        if (asc) {
            orderBy.ascending();
        } else {
            orderBy.descending();
        }
        return orderBy;
    }

    public static void generateQueryArgsSortBy(QueryArgs args) {
        args.orderByList.clear();
        boolean asc = args.order == AscDescOrder.Asc;
        args.orderByList.add(ascDescOrder(OrderBy.fromProperty(Metadata_Table.name), true));
        switch (args.sortBy) {
            case None:
            case Name:
                break;
            case FileType:
                args.orderByList.add(ascDescOrder(OrderBy.fromProperty(Metadata_Table.type), asc));
                break;
            case Size:
                args.orderByList.add(ascDescOrder(OrderBy.fromProperty(Metadata_Table.size), asc));
                break;
            case CreationTime:
                args.orderByList.add(ascDescOrder(OrderBy.fromProperty(Metadata_Table.lastModified), asc));
                break;
            case BookTitle:
                args.orderByList.add(ascDescOrder(OrderBy.fromProperty(Metadata_Table.title), asc));
                break;
            case Author:
                args.orderByList.add(ascDescOrder(OrderBy.fromProperty(Metadata_Table.authors), asc));
                break;
            case Publisher:
                args.orderByList.add(ascDescOrder(OrderBy.fromProperty(Metadata_Table.publisher), asc));
                break;
            case LastOpenTime:
                args.orderByList.add(ascDescOrder(OrderBy.fromProperty(Metadata_Table.lastAccess), asc));
                break;
            case RecentlyRead:
                args.orderByList.add(ascDescOrder(OrderBy.fromProperty(Metadata_Table.lastAccess), asc));
                break;
            case Total:
                args.orderByList.add(ascDescOrder(OrderBy.fromProperty(Metadata_Table.lastAccess), asc));
                break;
            case StartTime:
                args.orderByList.add(ascDescOrder(OrderBy.fromProperty(Metadata_Table.lastAccess), asc));
                break;
            case InstallTime:
                args.orderByList.add(ascDescOrder(OrderBy.fromProperty(Metadata_Table.lastAccess), asc));
                break;
        }
    }

}
