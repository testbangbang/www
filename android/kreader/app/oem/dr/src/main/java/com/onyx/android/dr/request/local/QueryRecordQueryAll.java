package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.QueryRecordEntity;
import com.onyx.android.dr.util.SortClass;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.Collections;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/6.
 */
public class QueryRecordQueryAll extends BaseDataRequest {
    private List<QueryRecordEntity> queryRecordList;

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        queryQueryRecordList();
    }

    public List<QueryRecordEntity> getList() {
        SortClass sort = new SortClass();
        Collections.sort(queryRecordList, sort);
        return queryRecordList;
    }

    public void setList(List<QueryRecordEntity> queryRecordList) {
        this.queryRecordList = queryRecordList;
    }

    public void queryQueryRecordList() {
        List<QueryRecordEntity> queryRecordList = new Select().from(QueryRecordEntity.class).queryList();
        if (queryRecordList != null && queryRecordList.size() > 0) {
            setList(queryRecordList);
        }
    }
}
