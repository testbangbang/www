package com.onyx.android.dr.presenter;

import com.onyx.android.dr.data.GoodSentenceData;
import com.onyx.android.dr.interfaces.GoodSentenceView;
import com.onyx.android.dr.request.local.GoodSentenceExcerptQuery;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

/**
 * Created by zhouzhiming on 2017/7/12.
 */
public class GoodSentencePresenter {
    private final GoodSentenceData goodSentenceData;
    private GoodSentenceView goodSentenceView;

    public GoodSentencePresenter(GoodSentenceView goodSentenceView) {
        this.goodSentenceView = goodSentenceView;
        goodSentenceData = new GoodSentenceData();
    }

    public void getAllGoodSentenceData() {
        final GoodSentenceExcerptQuery req = new GoodSentenceExcerptQuery();
        goodSentenceData.getAllGoodSentence(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                goodSentenceView.setGoodSentenceData(req.getGoodSentenceList());
            }
        });
    }
}
