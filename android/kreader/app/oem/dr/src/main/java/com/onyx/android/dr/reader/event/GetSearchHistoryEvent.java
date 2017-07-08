package com.onyx.android.dr.reader.event;

import com.onyx.android.sdk.data.model.SearchHistory;

import java.util.List;

/**
 * Created by li on 2017/6/6.
 */

public class GetSearchHistoryEvent {
    private List<SearchHistory> searchHistoryList;

    public GetSearchHistoryEvent(List<SearchHistory> searchHistoryList) {
        this.searchHistoryList = searchHistoryList;
    }

    public List<SearchHistory> getSearchHistoryList() {
        return searchHistoryList;
    }
}
