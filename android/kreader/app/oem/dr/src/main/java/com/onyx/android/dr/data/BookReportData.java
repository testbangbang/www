package com.onyx.android.dr.data;

import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.request.cloud.AddCommentRequest;
import com.onyx.android.dr.request.cloud.AddInformalCommentRequest;
import com.onyx.android.dr.request.cloud.BringOutBookReportRequest;
import com.onyx.android.dr.request.cloud.CreateBookReportRequest;
import com.onyx.android.dr.request.cloud.DeleteBookReportRequest;
import com.onyx.android.dr.request.cloud.GetBookReportListRequest;
import com.onyx.android.dr.request.cloud.GetBookReportRequest;
import com.onyx.android.dr.request.cloud.GetSharedImpressionRequest;
import com.onyx.android.dr.request.cloud.GetSharedInformalRequest;
import com.onyx.android.dr.request.cloud.ShareBookReportRequest;
import com.onyx.android.dr.request.local.ReaderResponseInsert;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.data.request.data.db.GetBookLibraryIdRequest;

import java.util.ArrayList;

/**
 * Created by li on 2017/9/19.
 */

public class BookReportData {
    private ArrayList<String> htmlTitle;

    public void submitRequest(Context context, final BaseDataRequest req, final BaseCallback callBack) {
        DataManager dataManager = DRApplication.getDataManager();
        dataManager.submit(context, req, callBack);
    }

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

    public void addInformalComment(AddInformalCommentRequest rq, BaseCallback callback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), rq, callback);
    }

    public void getLibraryId(GetBookLibraryIdRequest rq, BaseCallback callback) {
        DRApplication.getDataManager().submit(DRApplication.getInstance(), rq, callback);
    }

    public void shareImpression(ShareBookReportRequest rq, BaseCallback callback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), rq, callback);
    }

    public void getSharedImpressions(GetSharedImpressionRequest rq, BaseCallback callback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), rq, callback);
    }

    public void getSharedInformal(GetSharedInformalRequest rq, BaseCallback callback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), rq, callback);
    }

    public void insertReaderResponse(Context context, ReaderResponseInsert req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public ArrayList<String> getHtmlTitle(Context context) {
        htmlTitle = new ArrayList<String>();
        htmlTitle.add(context.getString(R.string.book_report_list_time));
        htmlTitle.add(context.getString(R.string.book_report_list_book_name));
        htmlTitle.add(context.getString(R.string.book_report_list_pages));
        htmlTitle.add(context.getString(R.string.book_report_list_summary));
        htmlTitle.add(context.getString(R.string.book_report_list_word_count));
        return htmlTitle;
    }
}
