package com.onyx.jdread.library.request;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.SearchHistory;
import com.onyx.android.sdk.data.rxrequest.data.db.RxBaseDBRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehai on 18-1-19.
 */

public class RxLoadSearchHistoryRequest extends RxBaseDBRequest {
    private List<SearchHistory> searchHistoryList = new ArrayList<>();
    private int limit;

    public RxLoadSearchHistoryRequest(DataManager dm,int limit) {
        super(dm);
        this.limit = limit;
    }

    @Override
    public RxLoadSearchHistoryRequest call() throws Exception {
        searchHistoryList.clear();
        searchHistoryList.addAll(getDataProvider().loadSearchHistory(limit));
        return this;
    }

    public List<SearchHistory> getSearchHistoryList() {
        return searchHistoryList;
    }
}
