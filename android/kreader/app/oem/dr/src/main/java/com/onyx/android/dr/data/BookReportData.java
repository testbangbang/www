package com.onyx.android.dr.data;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.request.cloud.AddCommentRequest;
import com.onyx.android.dr.request.cloud.BringOutBookReportRequest;
import com.onyx.android.dr.request.cloud.CreateBookReportRequest;
import com.onyx.android.dr.request.cloud.DeleteBookReportRequest;
import com.onyx.android.dr.request.cloud.GetBookReportListRequest;
import com.onyx.android.dr.request.cloud.GetBookReportRequest;
import com.onyx.android.dr.request.cloud.ShareBookReportRequest;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.request.data.db.GetBookLibraryIdRequest;

/**
 * Created by li on 2017/9/19.
 */

public class BookReportData {
    public void createImpression(CreateBookReportRequest rq, BaseCallback callback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), rq, callback);
    }

    public void getImpressionsList(GetBookReportListRequest rq, BaseCallback callback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), rq, callback);
    }

    public void deleteImpression(DeleteBookReportRequest rq, BaseCallback callback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), rq, callback);
    }

    public void bringOutReport(BringOutBookReportRequest rq, BaseCallback callback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), rq, callback);
    }

    public void getImpression(GetBookReportRequest rq, BaseCallback callback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), rq, callback);
    }

    public void addComment(AddCommentRequest rq, BaseCallback callback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), rq, callback);
    }

    public void getLibraryId(GetBookLibraryIdRequest rq, BaseCallback callback) {
        DRApplication.getDataManager().submit(DRApplication.getInstance(), rq, callback);
    }

    public void shareImpression(ShareBookReportRequest rq, BaseCallback callback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), rq, callback);
    }
}
