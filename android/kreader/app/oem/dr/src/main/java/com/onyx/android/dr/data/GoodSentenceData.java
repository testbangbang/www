package com.onyx.android.dr.data;


import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.request.local.GoodSentenceExcerptQuery;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.data.request.data.db.DataRequestChain;

/**
 * Created by zhouzhiming on 2017/7/12.
 */
public class GoodSentenceData {

    public void submitRequest(final BaseDataRequest req, final BaseCallback callBack) {
        final DataRequestChain requestChain = new DataRequestChain();
        requestChain.addRequest(req, callBack);
        requestChain.execute(DRApplication.getDataManager());
    }

    public void getAllGoodSentence(final GoodSentenceExcerptQuery req, final BaseCallback baseCallback) {
        submitRequest(req, baseCallback);
    }
}
