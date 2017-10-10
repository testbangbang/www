package com.onyx.android.dr.data;


import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.request.cloud.CreateReadingRateRequest;
import com.onyx.android.dr.request.cloud.RequestGetReadingRate;
import com.onyx.android.dr.request.local.ReadingRateExport;
import com.onyx.android.dr.request.local.ReadingRateInsert;
import com.onyx.android.dr.request.local.ReadingRateQueryAll;
import com.onyx.android.dr.request.local.ReadingRateQueryByTimeAndType;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

import java.util.ArrayList;

/**
 * Created by zhouzhiming on 2017/7/12.
 */
public class ReadingRateData {
    private ArrayList<String> htmlTitle;

    public void submitRequest(Context context, final BaseDataRequest req, final BaseCallback callBack) {
        DataManager dataManager = DRApplication.getDataManager();
        dataManager.submit(context, req, callBack);
    }

    public void getAllReadingRate(Context context, ReadingRateQueryAll req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void getDataByTimeAndType(Context context, ReadingRateQueryByTimeAndType req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void insertReadingRate(Context context, ReadingRateInsert req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void exportReadingRate(Context context, ReadingRateExport req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void createReadingRate(CreateReadingRateRequest req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }

    public void getReadingRate(RequestGetReadingRate req, BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, baseCallback);
    }

    public ArrayList<String> getHtmlTitle(Context context) {
        htmlTitle = new ArrayList<String>();
        htmlTitle.add(context.getString(R.string.book_report_list_time));
        htmlTitle.add(context.getString(R.string.book_report_list_book_name));
        htmlTitle.add(context.getString(R.string.time_horizon));
        htmlTitle.add(context.getString(R.string.language_type));
        htmlTitle.add(context.getString(R.string.read_summary_content));
        htmlTitle.add(context.getString(R.string.reader_response_content));
        htmlTitle.add(context.getString(R.string.reader_response_number));
        return htmlTitle;
    }
}
