package com.onyx.android.dr.data;


import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.request.local.GoodSentenceDeleteByTime;
import com.onyx.android.dr.request.local.GoodSentenceExport;
import com.onyx.android.dr.request.local.GoodSentenceInsert;
import com.onyx.android.dr.request.local.GoodSentenceQueryByPageNumber;
import com.onyx.android.dr.request.local.GoodSentenceQueryByReadingMatter;
import com.onyx.android.dr.request.local.GoodSentenceQueryByTime;
import com.onyx.android.dr.request.local.GoodSentenceQueryByType;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

import java.util.ArrayList;

/**
 * Created by zhouzhiming on 2017/7/12.
 */
public class GoodSentenceData {
    private ArrayList<String> htmlTitle;

    public void submitRequest(Context context, final BaseDataRequest req, final BaseCallback callBack) {
        DataManager dataManager = DRApplication.getDataManager();
        dataManager.submit(context, req, callBack);
    }

    public void getGoodSentenceByType(Context context, final GoodSentenceQueryByType req, final BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void getGoodSentenceByTime(Context context, final GoodSentenceQueryByTime req, final BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void getGoodSentenceByPageNumber(Context context, final GoodSentenceQueryByPageNumber req, final BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void getGoodSentenceByReadingMatter(Context context, final GoodSentenceQueryByReadingMatter req, final BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void insertGoodSentence(Context context, GoodSentenceInsert req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void deleteGoodSentence(Context context, GoodSentenceDeleteByTime req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void exportGoodSentence(Context context, GoodSentenceExport req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public ArrayList<String> setHtmlTitle(Context context) {
        htmlTitle = new ArrayList<String>();
        htmlTitle.add(context.getString(R.string.order_number));
        htmlTitle.add(context.getString(R.string.good_sentence_activity_good_sentence));
        htmlTitle.add(context.getString(R.string.good_sentence_activity_involved_reading_matter));
        htmlTitle.add(context.getString(R.string.good_sentence_activity_page_number));
        htmlTitle.add(context.getString(R.string.memorandum_activity_time));
        return htmlTitle;
    }
}
