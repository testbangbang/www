package com.onyx.android.dr.data;


import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.request.local.InformalEssayDelete;
import com.onyx.android.dr.request.local.InformalEssayInsert;
import com.onyx.android.dr.request.local.InformalEssayQueryByTime;
import com.onyx.android.dr.request.local.InformalEssayQueryByTitle;
import com.onyx.android.dr.request.local.ReadingRateExport;
import com.onyx.android.dr.request.local.ReadingRateQueryAll;
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

    public void getInformalEssayQueryByTitle(Context context, InformalEssayQueryByTitle req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void getInformalEssayByTime(Context context, InformalEssayQueryByTime req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void insertInformalEssay(Context context, InformalEssayInsert req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void deleteInformalEssay(Context context, InformalEssayDelete req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void exportReadingRate(Context context, ReadingRateExport req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public ArrayList<String> getHtmlTitle(Context context) {
        htmlTitle = new ArrayList<String>();
        htmlTitle.add(context.getString(R.string.infromal_essay_activity_time));
        htmlTitle.add(context.getString(R.string.infromal_essay_activity_title));
        htmlTitle.add(context.getString(R.string.infromal_essay_activity_word_number));
        htmlTitle.add(context.getString(R.string.infromal_essay_activity_content));
        return htmlTitle;
    }
}
