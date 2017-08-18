package com.onyx.android.dr.request.local;

import com.onyx.android.dr.common.Constants;
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
    private String type;
    public List<String> history = new ArrayList<>();

    public RequestSearchHistoryQuery(String type) {
        this.type = type;
    }

    @Override
    public void execute(DataManager manager) throws Exception {
        history.clear();
        for (SearchHistoryEntity searchHistoryEntity : queryHistory()) {
            switch (type) {
                case Constants.NAME_SEARCH:
                    history.add(searchHistoryEntity.name);
                    break;
                case Constants.AUTHOR_SEARCH:
                    history.add(searchHistoryEntity.author);
                    break;
            }
        }
    }

    private List<SearchHistoryEntity> queryHistory() {
        ConditionGroup conditions = null;
        switch (type) {
            case Constants.NAME_SEARCH:
                conditions = ConditionGroup.clause().and(SearchHistoryEntity_Table.name.isNotNull());
                break;
            case Constants.AUTHOR_SEARCH:
                conditions = ConditionGroup.clause().and(SearchHistoryEntity_Table.author.isNotNull());
                break;
        }
        List<SearchHistoryEntity> searchHistoryEntities = new Select().from(SearchHistoryEntity.class).where(conditions).queryList();
        Collections.reverse(searchHistoryEntities);
        return searchHistoryEntities;
    }
}
