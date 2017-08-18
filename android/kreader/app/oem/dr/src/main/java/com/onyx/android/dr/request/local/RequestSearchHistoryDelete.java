package com.onyx.android.dr.request.local;

import com.onyx.android.dr.common.Constants;
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
    private String type;

    public RequestSearchHistoryDelete(String type) {
        this.type = type;
    }

    @Override
    public void execute(DataManager helper) throws Exception {
        delete();
    }

    private void delete() {
        ConditionGroup conditions = null;
        switch (type) {
            case Constants.NAME_SEARCH:
                conditions = ConditionGroup.clause().and(SearchHistoryEntity_Table.name.isNotNull());
                break;
            case Constants.AUTHOR_SEARCH:
                conditions = ConditionGroup.clause().and(SearchHistoryEntity_Table.author.isNotNull());
                break;
        }
        new Delete().from(SearchHistoryEntity.class).where(conditions).query();
    }
}
