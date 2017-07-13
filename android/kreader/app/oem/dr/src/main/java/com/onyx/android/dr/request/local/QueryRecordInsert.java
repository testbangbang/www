package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.QueryRecordEntity;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/6.
 */
public class QueryRecordInsert extends BaseDataRequest {
    private QueryRecordEntity queryRecordInfo;
    private boolean weatherInsert = true;

    public QueryRecordInsert(QueryRecordEntity queryRecordEntity) {
        this.queryRecordInfo = queryRecordEntity;
    }

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        List<QueryRecordEntity> queryRecordList = queryQueryRecordList();
        if (queryRecordList != null && queryRecordList.size() > 0) {
            for (int i = 0; i < queryRecordList.size(); i++) {
                QueryRecordEntity queryRecordEntity = queryRecordList.get(i);
                if (queryRecordInfo.word.equals(queryRecordEntity.word)) {
                    queryRecordEntity.time = queryRecordInfo.time;
                    queryRecordEntity.update();
                    weatherInsert = false;
                    break;
                }
            }
            if (weatherInsert){
                queryRecordInfo.insert();
            }
        } else {
            queryRecordInfo.insert();
        }
    }

    public List<QueryRecordEntity> queryQueryRecordList() {
        List<QueryRecordEntity> queryRecordList = new Select().from(QueryRecordEntity.class).queryList();
        return queryRecordList;
    }
}
