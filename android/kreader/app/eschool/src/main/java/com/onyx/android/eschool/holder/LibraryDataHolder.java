package com.onyx.android.eschool.holder;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;

import com.onyx.android.eschool.R;
import com.onyx.android.eschool.SchoolApp;
import com.onyx.android.sdk.data.BookFilter;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by suicheng on 2017/4/15.
 */

public class LibraryDataHolder extends BaseDataHolder {
    private DataManager dataManager = SchoolApp.getDataManager();
    private FragmentManager fragmentManager;

    private Map<String, SortBy> sortByMap = new LinkedHashMap();
    private Map<String, BookFilter> filterMap = new LinkedHashMap<>();
    private SortBy currentSortBy = SortBy.Name;
    private BookFilter currentFilter = BookFilter.ALL;
    private SortOrder currentSortOrder = SortOrder.Desc;
    private int currentSortByIndex = 0;
    private int currentFilterByIndex = 0;

    private int queryLimit = 10;
    private long bookCount = 0;
    private List<Library> parentLibraryList = new ArrayList<>();
    private List<Library> libraryList = new ArrayList<>();
    private List<Metadata> bookList = new ArrayList<>();

    public LibraryDataHolder(Activity activity) {
        super(activity);
        fragmentManager = activity.getFragmentManager();
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
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

    public Map<String, SortBy> getSortByMap() {
        if (CollectionUtils.isNullOrEmpty(sortByMap)) {
            sortByMap.put(getContext().getString(R.string.by_name), SortBy.Name);
            sortByMap.put(getContext().getString(R.string.by_tittle), SortBy.BookTitle);
            sortByMap.put(getContext().getString(R.string.by_type), SortBy.FileType);
            sortByMap.put(getContext().getString(R.string.by_size), SortBy.Size);
            sortByMap.put(getContext().getString(R.string.by_creation_time), SortBy.CreationTime);
            sortByMap.put(getContext().getString(R.string.by_author), SortBy.Author);
        }
        return sortByMap;
    }

    public Map<String, BookFilter> getFilterMap() {
        if (CollectionUtils.isNullOrEmpty(filterMap)) {
            filterMap.put(getContext().getString(R.string.filter_all), BookFilter.ALL);
            filterMap.put(getContext().getString(R.string.filter_new_books), BookFilter.NEW);
            filterMap.put(getContext().getString(R.string.filter_reading), BookFilter.READING);
            filterMap.put(getContext().getString(R.string.filter_read), BookFilter.FINISHED);
            filterMap.put(getContext().getString(R.string.filter_tag), BookFilter.TAG);
        }
        return filterMap;
    }

    public void setFilterMap(Map<String, BookFilter> filterMap) {
        this.filterMap = filterMap;
    }

    public void setSortByMap(Map<String, SortBy> sortByMap) {
        this.sortByMap = sortByMap;
    }

    public void updateSortBy(SortBy sortBy, SortOrder sortOrder) {
        this.currentSortBy = sortBy;
        this.currentSortOrder = sortOrder;
    }

    public void updateFilterByBy(BookFilter filter, SortOrder sortOrder) {
        this.currentFilter = filter;
        this.currentSortOrder = sortOrder;
    }

    public QueryArgs getQueryArgs(int limit, int offset) {
        ConditionGroup conditionGroup = ConditionGroup.clause()
                .or(Metadata_Table.storageId.isNull());
        if (StringUtils.isNotBlank(SchoolApp.singleton().getSdcardCid())) {
            conditionGroup.or(Metadata_Table.storageId.is(SchoolApp.singleton().getSdcardCid()));
        }
        QueryArgs args = new QueryArgs(currentSortBy, currentSortOrder).appendFilter(currentFilter);
        args.limit = limit;
        args.offset = offset;
        args.libraryUniqueId = getLibraryIdString();
        QueryBuilder.generateQueryArgs(args);
        QueryBuilder.andWith(args.conditionGroup, conditionGroup);
        return QueryBuilder.generateMetadataInQueryArgs(args);
    }

    public QueryArgs getQueryArgs() {
        return getQueryArgs(queryLimit, 0);
    }

    public int getCurrentSortByIndex() {
        return currentSortByIndex;
    }

    public void setCurrentSortByIndex(int currentSortByIndex) {
        this.currentSortByIndex = currentSortByIndex;
    }

    public int getCurrentFilterByIndex() {
        return currentFilterByIndex;
    }

    public void setCurrentFilterByIndex(int currentFilterByIndex) {
        this.currentFilterByIndex = currentFilterByIndex;
    }

    public void setCurrentSortOrder(SortOrder sortOrder) {
        this.currentSortOrder = sortOrder;
    }

    public SortOrder getCurrentSortOrder() {
        return currentSortOrder;
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
