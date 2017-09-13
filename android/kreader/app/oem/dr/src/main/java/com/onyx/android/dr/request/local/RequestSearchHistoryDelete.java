package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.SearchHistoryEntity;
import com.onyx.android.dr.data.database.SearchHistoryEntity_Table;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.Delete;


/**
 * Created by hehai on 2016/12/19.
 */
public class RequestSearchHistoryDelete extends BaseDataRequest {

    public RequestSearchHistoryDelete() {
    }

    @Override
    public void execute(DataManager helper) throws Exception {
        delete();
    }

    private void delete() {
        ConditionGroup conditions = ConditionGroup.clause().and(SearchHistoryEntity_Table.name.isNotNull());
        new Delete().from(SearchHistoryEntity.class).where(conditions).query();
    }
}
