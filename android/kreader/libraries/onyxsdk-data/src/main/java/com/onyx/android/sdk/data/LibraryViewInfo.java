package com.onyx.android.sdk.data;

import android.graphics.Bitmap;

import com.facebook.common.references.CloseableReference;
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
    private QueryArgs queryArgs;
    private QueryPagination queryPagination = QueryPagination.create(3, 3);
    private LibraryDataModel libraryDataModel;
    private List<Library> libraryPath = new ArrayList<>();

    public LibraryViewInfo() {
        queryArgs = new QueryArgs();
        queryArgs.limit = queryLimit;
        queryPagination.setCurrentPage(0);
    }

    public static LibraryViewInfo create(int rows, int cols) {
        return new LibraryViewInfo(rows, cols, SortBy.Name, SortOrder.Asc);
    }

    public LibraryViewInfo(int row, int col, SortBy sortBy, SortOrder sortOrder) {
        queryArgs = new QueryArgs();
        queryArgs.limit = row * col;
        queryPagination.resize(row, col, 0);
        queryPagination.setCurrentPage(0);
        updateSortBy(sortBy, sortOrder);
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

    public QueryArgs pageQueryArgs(int page) {
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

    public QueryArgs firstPage() {
        return gotoPage(0);
    }

    public QueryArgs lastPage() {
        return gotoPage(queryPagination.lastPage());
    }

    public String getSdcardCid() {
        return EnvironmentUtil.getRemovableSDCardCid();
    }

    private ConditionGroup storageIdCondition() {
        ConditionGroup conditionGroup = ConditionGroup.clause()
                .or(Metadata_Table.storageId.isNull());
        String cid = getSdcardCid();
        if (StringUtils.isNotBlank(cid)) {
            conditionGroup.or(Metadata_Table.storageId.is(cid));
        }
        return conditionGroup;
    }

    public QueryArgs libraryQuery(int limit, int offset) {
        QueryArgs args = libraryQuery(getLibraryIdString());
        args.limit = limit;
        args.offset = offset;
        return args;
    }

    public QueryArgs libraryQuery(String libraryId) {
        QueryArgs args = new QueryArgs(queryArgs.sortBy, queryArgs.order).appendFilter(queryArgs.filter);
        args.limit = queryLimit;
        args.offset = 0;
        args.libraryUniqueId = libraryId;
        QueryBuilder.generateQueryArgs(args);
        QueryBuilder.andWith(args.conditionGroup, storageIdCondition());
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
        clearThumbnailMap(getLibraryDataModel());
        this.libraryDataModel = libraryDataModel;
    }

    public LibraryDataModel getPageLibraryDataModel(LibraryDataModel dataModel) {
        setLibraryDataModel(dataModel);
        LibraryDataModel pageDataModel = new LibraryDataModel();
        int currentPage = queryPagination.getCurrentPage();
        int itemsPerPage = queryPagination.itemsPerPage();
        if (currentPage > libraryDataModel.libraryCount / itemsPerPage) {
            pageDataModel.visibleLibraryList = new ArrayList<>();
            pageDataModel.visibleBookList = libraryDataModel.visibleBookList;
        } else {
            int position = currentPage * itemsPerPage;
            for (int i = position;
                 (i < libraryDataModel.libraryCount && i < (currentPage + 1) * itemsPerPage);
                 i++) {
                pageDataModel.visibleLibraryList.add(libraryDataModel.visibleLibraryList.get(i));
            }
            int size = itemsPerPage - CollectionUtils.getSize(pageDataModel.visibleLibraryList);
            for (int i = 0; (i < size && i < CollectionUtils.getSize(libraryDataModel.visibleBookList)); i++) {
                pageDataModel.visibleBookList.add(libraryDataModel.visibleBookList.get(i));
            }
        }
        pageDataModel.bookCount = libraryDataModel.bookCount;
        pageDataModel.libraryCount = libraryDataModel.libraryCount;
        pageDataModel.thumbnailMap = libraryDataModel.thumbnailMap;
        return pageDataModel;
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

    public void setLibraryPathList(List<Library> newPathList) {
        this.libraryPath = newPathList;
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

    public void setQueryLimit(int limit) {
        this.queryLimit = limit;
    }

    public int getQueryLimit() {
        return queryLimit;
    }

    public void clearThumbnailMap(LibraryDataModel dataModel) {
        if (dataModel == null || CollectionUtils.isNullOrEmpty(dataModel.thumbnailMap)) {
            return;
        }
        for (CloseableReference<Bitmap> refBitmap : dataModel.thumbnailMap.values()) {
            refBitmap.close();
        }
    }
}
