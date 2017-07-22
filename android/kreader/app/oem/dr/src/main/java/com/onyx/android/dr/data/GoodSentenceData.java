package com.onyx.android.dr.data;


import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.request.local.GoodSentenceExcerptInsert;
import com.onyx.android.dr.request.local.GoodSentenceExcerptQueryAll;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

/**
 * Created by zhouzhiming on 2017/7/12.
 */
public class GoodSentenceData {

    public void submitRequest(Context context, final BaseDataRequest req, final BaseCallback callBack) {
        DataManager dataManager = DRApplication.getDataManager();
        dataManager.submit(context, req, callBack);
    }

    public void getAllGoodSentence(Context context, final GoodSentenceExcerptQueryAll req, final BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void insertGoodSentence(Context context, GoodSentenceExcerptInsert req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }
}
