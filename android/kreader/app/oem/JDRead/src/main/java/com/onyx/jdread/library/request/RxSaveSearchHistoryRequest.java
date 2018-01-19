package com.onyx.jdread.library.request;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.SearchHistory;
import com.onyx.android.sdk.data.rxrequest.data.db.RxBaseDBRequest;

/**
 * Created by hehai on 18-1-19.
 */

public class RxSaveSearchHistoryRequest extends RxBaseDBRequest {
    private SearchHistory searchHistory;

    public RxSaveSearchHistoryRequest(DataManager dm, SearchHistory searchHistory) {
        super(dm);
        this.searchHistory = searchHistory;
    }

    @Override
    public RxSaveSearchHistoryRequest call() throws Exception {
        getDataProvider().saveSearchHistory(getAppContext(), searchHistory);
        return this;
    }
}
