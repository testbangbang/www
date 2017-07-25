package com.onyx.android.dr.data;


import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.request.local.InfromalEssayDelete;
import com.onyx.android.dr.request.local.InfromalEssayInsert;
import com.onyx.android.dr.request.local.InfromalEssayQueryAll;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

/**
 * Created by zhouzhiming on 2017/7/12.
 */
public class InfromalEssayData {

    public void submitRequest(Context context, final BaseDataRequest req, final BaseCallback callBack) {
        DataManager dataManager = DRApplication.getDataManager();
        dataManager.submit(context, req, callBack);
    }

    public void getAllInfromalEssay(Context context, InfromalEssayQueryAll req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void insertInfromalEssay(Context context, InfromalEssayInsert req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void deleteInfromalEssay(Context context, InfromalEssayDelete req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }
}
