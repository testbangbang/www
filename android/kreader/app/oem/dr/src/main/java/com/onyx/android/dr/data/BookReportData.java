package com.onyx.android.dr.data;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.request.cloud.CreateBookReportRequest;
import com.onyx.android.sdk.common.request.BaseCallback;

/**
 * Created by li on 2017/9/19.
 */

public class BookReportData {
    public void createImpression(CreateBookReportRequest rq, BaseCallback callback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), rq, callback);
    }
}
