package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.SearchHistoryEntity;
import com.onyx.android.dr.data.database.SearchHistoryEntity_Table;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by hehai on 2016/12/19.
 */
public class RequestSearchHistoryQuery extends BaseDataRequest {
    public List<String> history = new ArrayList<>();

    public RequestSearchHistoryQuery() {
    }

    @Override
    public void execute(DataManager manager) throws Exception {
        history.clear();
        for (SearchHistoryEntity searchHistoryEntity : queryHistory()) {
            history.add(searchHistoryEntity.name);
        }
    }

    private List<SearchHistoryEntity> queryHistory() {
        ConditionGroup conditions = ConditionGroup.clause().and(SearchHistoryEntity_Table.name.isNotNull());
        List<SearchHistoryEntity> searchHistoryEntities = new Select().from(SearchHistoryEntity.class).where(conditions).queryList();
        Collections.reverse(searchHistoryEntities);
        return searchHistoryEntities;
    }
}
