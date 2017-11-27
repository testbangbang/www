package com.onyx.android.dr.data;

import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.request.cloud.DeleteArticlePushRequest;
import com.onyx.android.dr.request.cloud.RequestGetArticlePush;
import com.onyx.android.sdk.common.request.BaseCallback;

import java.util.ArrayList;

/**
 * Created by hehai on 17-9-20.
 */

public class ArticlePushData {
    private ArrayList<String> htmlTitle;

    public void getArticlePush(RequestGetArticlePush req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }

    public void removeArticlePush(DeleteArticlePushRequest req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
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
