package com.onyx.android.dr.reader.data;

import com.alibaba.fastjson.JSON;
import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.reader.requests.RequestReadBookInsert;
import com.onyx.android.dr.reader.requests.RequestReadSummaryInsert;
import com.onyx.android.dr.reader.requests.RequestReadSummaryQuery;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

import java.util.List;

/**
 * Created by hehai on 17-8-24.
 */

public class ReadSummaryData {
    private List<ReadSummaryNewWordReviewBean> newWordReviewBeenList;
    private List<ReadSummaryGoodSentenceReviewBean> readSummaryGoodSentenceReviewBeen;
    private ReadSummaryEntity readSummaryEntity;

    public void getReadSummary(final RequestReadSummaryQuery req, final BaseCallback baseCallback) {
        DRApplication.getDataManager().submit(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                readSummaryEntity = req.getReadSummaryEntity();
                if (readSummaryEntity != null) {
                    String newWordList = readSummaryEntity.newWordList;
                    String goodSentenceList = readSummaryEntity.goodSentenceList;
                    newWordReviewBeenList = JSON.parseArray(newWordList, ReadSummaryNewWordReviewBean.class);
                    readSummaryGoodSentenceReviewBeen = JSON.parseArray(goodSentenceList, ReadSummaryGoodSentenceReviewBean.class);
                }
                invoke(baseCallback, request, e);
            }
        });
    }

    public List<ReadSummaryNewWordReviewBean> getNewWordReviewBeenList() {
        return newWordReviewBeenList;
    }

    public List<ReadSummaryGoodSentenceReviewBean> getReadSummaryGoodSentenceReviewBeen() {
        return readSummaryGoodSentenceReviewBeen;
    }

    public void saveReadSummary(RequestReadSummaryInsert req, BaseCallback baseCallback) {
        DRApplication.getDataManager().submit(DRApplication.getInstance(), req, baseCallback);
    }

    public void saveReadBook(RequestReadBookInsert req, BaseCallback baseCallback) {
        DRApplication.getDataManager().submit(DRApplication.getInstance(), req, baseCallback);
    }
}
