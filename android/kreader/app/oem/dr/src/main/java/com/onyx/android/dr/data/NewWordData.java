package com.onyx.android.dr.data;


import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.request.local.NewWordDelete;
import com.onyx.android.dr.request.local.NewWordExport;
import com.onyx.android.dr.request.local.NewWordInsert;
import com.onyx.android.dr.request.local.NewWordQueryByPageNumber;
import com.onyx.android.dr.request.local.NewWordQueryByReadingMatter;
import com.onyx.android.dr.request.local.NewWordQueryByTime;
import com.onyx.android.dr.request.local.NewWordQueryByType;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

import java.util.ArrayList;

/**
 * Created by zhouzhiming on 2017/7/12.
 */
public class NewWordData {
    private ArrayList<String> newWordTitle;

    public void submitRequest(Context context, final BaseDataRequest req, final BaseCallback callBack) {
        DataManager dataManager = DRApplication.getDataManager();
        dataManager.submit(context, req, callBack);
    }

    public void getAllNewWordByType(Context context, NewWordQueryByType req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void getAllNewWordByReadingMatter(Context context, NewWordQueryByReadingMatter req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void getAllNewWordByPageNumber(Context context, NewWordQueryByPageNumber req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void getNewWordByTime(Context context, NewWordQueryByTime req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void insertNewWord(Context context, NewWordInsert req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void deleteNewWord(Context context, NewWordDelete req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void exportNewWord(Context context, NewWordExport req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public ArrayList<String> setHtmlTitle(Context context) {
        newWordTitle = new ArrayList<String>();
        newWordTitle.add(context.getString(R.string.memorandum_activity_time));
        newWordTitle.add(context.getString(R.string.new_word_activity_new_word));
        newWordTitle.add(context.getString(R.string.new_word_activity_dictionaryLookup));
        newWordTitle.add(context.getString(R.string.good_sentence_activity_involved_reading_matter));
        return newWordTitle;
    }
}
