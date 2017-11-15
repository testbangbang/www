package com.onyx.android.dr.data;


import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.request.local.MemorandumDelete;
import com.onyx.android.dr.request.local.MemorandumExport;
import com.onyx.android.dr.request.local.MemorandumInsert;
import com.onyx.android.dr.request.local.MemorandumQueryAll;
import com.onyx.android.dr.request.local.MemorandumQueryByTime;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

import java.util.ArrayList;

/**
 * Created by zhouzhiming on 2017/7/12.
 */
public class MemorandumData {
    private ArrayList<String> htmlTitle;

    public void submitRequest(Context context, final BaseDataRequest req, final BaseCallback callBack) {
        DataManager dataManager = DRApplication.getDataManager();
        dataManager.submit(context, req, callBack);
    }

    public void getAllMemorandum(Context context, MemorandumQueryAll req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void getMemorandumByTime(Context context, MemorandumQueryByTime req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void insertMemorandum(Context context, MemorandumInsert req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void deleteMemorandum(Context context, MemorandumDelete req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void exportMemorandum(Context context, MemorandumExport req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public ArrayList<String> setHtmlTitle(Context context) {
        htmlTitle = new ArrayList<String>();
        htmlTitle.add(context.getString(R.string.memorandum_activity_time));
        htmlTitle.add(context.getString(R.string.memorandum_activity_time_quantum));
        htmlTitle.add(context.getString(R.string.memorandum_activity_matter));
        return htmlTitle;
    }
}
