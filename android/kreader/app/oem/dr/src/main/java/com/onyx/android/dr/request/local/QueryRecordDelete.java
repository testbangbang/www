package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.QueryRecordEntity;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Delete;

/**
 * Created by zhouzhiming on 2017/7/5.
 */
public class QueryRecordDelete extends BaseDataRequest {

    public QueryRecordDelete() {
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        clearNewWord();
    }

    private void clearNewWord() {
        new Delete().from(QueryRecordEntity.class).queryList();
    }
}
