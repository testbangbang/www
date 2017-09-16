package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.SearchHistoryEntity;
import com.onyx.android.dr.data.database.SearchHistoryEntity_Table;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Delete;

/**
 * Created by hehai on 2016/12/3.
 */
public class RequestSearchHistoryInsert extends BaseDataRequest {
    private String history;

    public RequestSearchHistoryInsert(String history) {
        this.history = history;
    }

    @Override
    public void execute(DataManager helper) throws Exception {
        insert();
    }

    public void insert() {
        new Delete().from(SearchHistoryEntity.class).where(SearchHistoryEntity_Table.name.eq(history)).query();
        SearchHistoryEntity searchHistoryEntity = new SearchHistoryEntity();
        searchHistoryEntity.name = history;
        searchHistoryEntity.insert();
    }
}

