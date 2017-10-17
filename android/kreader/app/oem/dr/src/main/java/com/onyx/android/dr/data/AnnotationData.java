package com.onyx.android.dr.data;

import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.request.local.ExportAnnotationListRequest;
import com.onyx.android.dr.request.local.RequestGetAnnotationList;
import com.onyx.android.dr.request.local.RequestRemoveAnnotationList;
import com.onyx.android.sdk.common.request.BaseCallback;

import java.util.ArrayList;

/**
 * Created by hehai on 17-9-27.
 */

public class AnnotationData {
    private ArrayList<String> htmlTitle;

    public void getAnnotationList(RequestGetAnnotationList req, BaseCallback baseCallback) {
        DRApplication.getDataManager().submit(DRApplication.getInstance(), req, baseCallback);
    }

    public void removeAnnotation(RequestRemoveAnnotationList req, BaseCallback baseCallback) {
        DRApplication.getDataManager().submit(DRApplication.getInstance(), req, baseCallback);
    }

    public void exportAnnotation(ExportAnnotationListRequest rq, BaseCallback callback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), rq, callback);
    }

    public ArrayList<String> getHtmlTitle(Context context) {
        htmlTitle = new ArrayList<>();
        htmlTitle.add(context.getString(R.string.book_report_list_time));
        htmlTitle.add(context.getString(R.string.book_report_list_book_name));
        htmlTitle.add(context.getString(R.string.library_name));
        htmlTitle.add(context.getString(R.string.postil_count));
        return htmlTitle;
    }
}
