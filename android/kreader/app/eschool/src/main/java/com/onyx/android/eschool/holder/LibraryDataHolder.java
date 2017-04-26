package com.onyx.android.eschool.holder;

import android.content.Context;

import com.onyx.android.eschool.R;
import com.onyx.android.sdk.data.BookFilter;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2017/4/15.
 */

public class LibraryDataHolder extends BaseDataHolder {

    private DataManager dataManager = new DataManager();
    private QueryArgs queryArgs = new QueryArgs();

    private int queryLimit = 10;
    private long bookCount = 0;
    private List<Library> parentLibraryList = new ArrayList<>();
    private List<Library> libraryList = new ArrayList<>();
    private List<Metadata> bookList = new ArrayList<>();

    public LibraryDataHolder(Context context) {
        super(context);
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public List<Library> getLibraryList() {
        return libraryList;
    }

    public void setLibraryList(List<Library> libraryList) {
        this.libraryList = libraryList;
    }

    public List<Metadata> getBookList() {
        return bookList;
    }

    public void setBookList(List<Metadata> bookList) {
        this.bookList = bookList;
    }

    public long getBookCount() {
        return bookCount;
    }

    public void setBookCount(long bookCount) {
        this.bookCount = bookCount;
    }

    public void updateSortBy(SortBy sortBy, SortOrder sortOrder) {
        queryArgs.sortBy = sortBy;
        queryArgs.order = sortOrder;
    }

    public void updateFilterBy(BookFilter filter, SortOrder sortOrder) {
        queryArgs.filter = filter;
        queryArgs.order = sortOrder;
    }

    public void updateQueryArgs(QueryArgs args) {
        this.queryArgs = args;
    }

    public QueryArgs getCurrentQueryArgs() {
        return queryArgs;
    }

    public String getSdcardCid() {
        return EnvironmentUtil.getRemovableSDCardCid();
    }

    public QueryArgs getQueryArgs(int limit, int offset) {
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
        updateQueryArgs(args);
        QueryBuilder.generateQueryArgs(args);
        QueryBuilder.andWith(args.conditionGroup, conditionGroup);
        return QueryBuilder.generateMetadataInQueryArgs(args);
    }

    public QueryArgs getQueryArgs() {
        return getQueryArgs(queryLimit, 0);
    }

    public void setCurrentSortOrder(SortOrder sortOrder) {
        this.queryArgs.order = sortOrder;
    }

    public SortOrder getCurrentSortOrder() {
        return queryArgs.order;
    }

    public int getQueryLimit() {
        return queryLimit;
    }

    public void setQueryLimit(int queryLimit) {
        this.queryLimit = queryLimit;
    }

    public String getLibraryIdString() {
        if (CollectionUtils.isNullOrEmpty(parentLibraryList)) {
            return null;
        }
        return parentLibraryList.get(parentLibraryList.size() - 1).getIdString();
    }

    public List<Library> getParentLibraryList() {
        return parentLibraryList;
    }
}
