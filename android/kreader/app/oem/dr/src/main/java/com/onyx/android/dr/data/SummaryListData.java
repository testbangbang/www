package com.onyx.android.dr.data;

import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.request.local.ExportSummaryRequest;
import com.onyx.android.dr.request.local.RequestSummaryDelete;
import com.onyx.android.dr.request.local.RequestSummaryQueryAll;
import com.onyx.android.sdk.common.request.BaseCallback;

import java.util.ArrayList;

/**
 * Created by hehai on 17-9-20.
 */

public class SummaryListData {
    private ArrayList<String> htmlTitle;

    public void getSummaryList(RequestSummaryQueryAll req, BaseCallback baseCallback) {
        DRApplication.getDataManager().submit(DRApplication.getInstance(), req, baseCallback);
    }

    public void removeSummary(RequestSummaryDelete req, BaseCallback baseCallback) {
        DRApplication.getDataManager().submit(DRApplication.getInstance(), req, baseCallback);
    }

    public void exportSummary(ExportSummaryRequest rq, BaseCallback callback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), rq, callback);
    }

    public ArrayList<String> getHtmlTitle(Context context) {
        htmlTitle = new ArrayList<>();
        htmlTitle.add(context.getString(R.string.book_report_list_time));
        htmlTitle.add(context.getString(R.string.book_report_list_book_name));
        htmlTitle.add(context.getString(R.string.book_report_list_pages));
        htmlTitle.add(context.getString(R.string.book_report_list_summary));
        return htmlTitle;
    }
}