package com.onyx.android.dr.data;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.request.local.RequestSummaryDelete;
import com.onyx.android.dr.request.local.RequestSummaryQueryAll;
import com.onyx.android.sdk.common.request.BaseCallback;

/**
 * Created by hehai on 17-9-20.
 */

public class SummaryListData {
    public void getSummaryList(RequestSummaryQueryAll req, BaseCallback baseCallback) {
        DRApplication.getDataManager().submit(DRApplication.getInstance(), req, baseCallback);
    }

    public void removeSummary(RequestSummaryDelete req, BaseCallback baseCallback) {
        DRApplication.getDataManager().submit(DRApplication.getInstance(), req, baseCallback);
    }
}
