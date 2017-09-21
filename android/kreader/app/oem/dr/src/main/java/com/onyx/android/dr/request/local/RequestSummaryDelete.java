package com.onyx.android.dr.request.local;

import com.onyx.android.dr.reader.data.ReadSummaryEntity;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

import java.util.List;

/**
 * Created by hehai on 17-1-19.
 */

public class RequestSummaryDelete extends BaseDataRequest {
    private List<ReadSummaryEntity> summaryList;

    public RequestSummaryDelete(List<ReadSummaryEntity> summaryList) {
        this.summaryList = summaryList;
    }

    @Override
    public void execute(DataManager helper) throws Exception {
        delete();
    }

    private void delete() {
        for (ReadSummaryEntity entity : summaryList) {
            entity.delete();
        }
    }
}
