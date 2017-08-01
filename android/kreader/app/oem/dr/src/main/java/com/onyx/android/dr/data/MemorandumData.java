package com.onyx.android.dr.data;


import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.request.local.MemorandumDelete;
import com.onyx.android.dr.request.local.MemorandumInsert;
import com.onyx.android.dr.request.local.MemorandumQueryAll;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

/**
 * Created by zhouzhiming on 2017/7/12.
 */
public class MemorandumData {

    public void submitRequest(Context context, final BaseDataRequest req, final BaseCallback callBack) {
        DataManager dataManager = DRApplication.getDataManager();
        dataManager.submit(context, req, callBack);
    }

    public void getAllMemorandum(Context context, MemorandumQueryAll req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void insertMemorandum(Context context, MemorandumInsert req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void deleteMemorandum(Context context, MemorandumDelete req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }
}
