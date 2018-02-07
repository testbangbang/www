package com.onyx.jdread.setting.request;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryBase;
import com.onyx.android.sdk.data.rxrequest.data.db.RxBaseDBRequest;
import com.onyx.android.sdk.data.utils.StoreUtils;
import com.onyx.jdread.setting.data.database.FeedbackRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2018/2/1.
 */
public class RxFeedbackRecordListLoadRequest extends RxBaseDBRequest {

    private List<FeedbackRecord> recordList = new ArrayList<>();
    private QueryBase queryArgs;

    public RxFeedbackRecordListLoadRequest(DataManager dataManager, QueryBase queryArgs) {
        super(dataManager);
        this.queryArgs = queryArgs;
    }

    public List<FeedbackRecord> getRecordList() {
        return recordList;
    }

    @Override
    public RxFeedbackRecordListLoadRequest call() throws Exception {
        recordList = fetchData();
        return this;
    }

    private List<FeedbackRecord> fetchData() {
        return fetchDataFromLocal();
    }

    private List<FeedbackRecord> fetchDataFromCloud() throws Exception {
        // TODO: 2018/2/4
        return new ArrayList<>();
    }

    private List<FeedbackRecord> fetchDataFromLocal() {
        return StoreUtils.queryDataList(FeedbackRecord.class, queryArgs);
    }
}
