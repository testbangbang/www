package com.onyx.android.eschool.model;

import com.onyx.android.sdk.data.BookFilter;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryPagination;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/4/23.
 */
public class LibraryViewInfo {
    private int queryLimit = 9;
    private QueryArgs queryArgs;;
    private QueryPagination queryPagination = QueryPagination.create(3, 3);
    private LibraryDataModel libraryDataModel;
    private List<Library> libraryPath = new ArrayList<>();

    public LibraryViewInfo() {
        queryArgs = new QueryArgs();
        queryArgs.limit = queryLimit;
        queryPagination.setCurrentPage(0);
    }

    public int getOffset(int currentPage) {
        LibraryDataModel libraryDataModel = getLibraryDataModel();
        int itemsPerPage = queryPagination.itemsPerPage();
        int offset = currentPage * itemsPerPage - libraryDataModel.libraryCount;
        if (offset < 0) {
            if (currentPage <= libraryDataModel.libraryCount / itemsPerPage) {
                offset = 0;
            } else {
                offset = itemsPerPage + offset;
            }
        }
        return offset;
    }

    public int getOffset() {
        return getOffset(queryPagination.getCurrentPage());
    }

    public QueryArgs nextPage() {
        queryArgs.offset = getOffset();
        return queryArgs;
    }

    public QueryArgs prevPage() {
        queryArgs.offset = getOffset();
        return queryArgs;
    }

    public QueryArgs preLoadingPage(int page) {
        QueryArgs args = new QueryArgs(queryArgs.sortBy, queryArgs.order);
        args.limit = queryLimit;
        args.offset = getOffset(page);
        args.orderByList.addAll(queryArgs.orderByList);
        args.conditionGroup = queryArgs.conditionGroup;
        return args;
    }

    public QueryArgs gotoPage(int page) {
        queryPagination.setCurrentPage(page);
        queryArgs.offset = getOffset(page);
        return queryArgs;
    }

    public String getSdcardCid() {
        return EnvironmentUtil.getRemovableSDCardCid();
    }

    public QueryArgs libraryQuery(int limit, int offset) {
        ConditionGroup conditionGroup = ConditionGroup.clause()
                .or(Metadata_Table.storageId.isNull());
        String cid = getSdcardCid();
        if (StringUtils.isNotBlank(cid)) {
            conditionGroup.or(Metadata_Table.storageId.is(cid));
        }
        QueryArgs args = new QueryArgs(queryArgs.sortBy, queryArgs.order).appendFilter(queryArgs.filter);
        args.limit = limit;
        args.offset = offset;
        args.libraryUniqueId = getLibraryIdString();
        QueryBuilder.generateQueryArgs(args);
        QueryBuilder.andWith(args.conditionGroup, conditionGroup);
        updateQueryArgs(args);
        return QueryBuilder.generateMetadataInQueryArgs(args);
    }

    public QueryArgs libraryQuery() {
        return libraryQuery(queryLimit, 0);
    }

    public QueryArgs getCurrentQueryArgs() {
        return this.queryArgs;
    }

    public void updateQueryArgs(QueryArgs args) {
        this.queryArgs = args;
    }

    public LibraryDataModel getLibraryDataModel() {
        return libraryDataModel;
    }

    public void setLibraryDataModel(LibraryDataModel libraryDataModel) {
        this.libraryDataModel = libraryDataModel;
    }

    public QueryPagination getQueryPagination() {
        return this.queryPagination;
    }

    public void setQueryPagination(QueryPagination queryPagination) {
        this.queryPagination = queryPagination;
    }

    public String getLibraryIdString() {
        if (CollectionUtils.isNullOrEmpty(libraryPath)) {
            return null;
        }
        return libraryPath.get(libraryPath.size() - 1).getIdString();
    }

    public List<Library> getLibraryPathList() {
        return libraryPath;
    }

    public void setCurrentSortOrder(SortOrder sortOrder) {
        this.queryArgs.order = sortOrder;
    }

    public SortOrder getCurrentSortOrder() {
        return queryArgs.order;
    }

    public void updateSortBy(SortBy sortBy, SortOrder sortOrder) {
        queryArgs.sortBy = sortBy;
        queryArgs.order = sortOrder;
    }

    public void updateFilterBy(BookFilter filter, SortOrder sortOrder) {
        queryArgs.filter = filter;
        queryArgs.order = sortOrder;
    }
}
