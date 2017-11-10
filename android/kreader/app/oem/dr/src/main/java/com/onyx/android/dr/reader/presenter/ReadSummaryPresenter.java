package com.onyx.android.dr.reader.presenter;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.reader.base.ReadSummaryView;
import com.onyx.android.dr.reader.data.ReadSummaryData;
import com.onyx.android.dr.reader.data.ReadSummaryEntity;
import com.onyx.android.dr.reader.requests.RequestReadSummaryInsert;
import com.onyx.android.dr.reader.requests.RequestReadSummaryQuery;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

/**
 * Created by hehai on 17-8-24.
 */

public class ReadSummaryPresenter {
    private ReadSummaryView readSummaryView;
    private ReadSummaryData readSummaryData;

    public ReadSummaryPresenter(ReadSummaryView readSummaryView) {
        this.readSummaryView = readSummaryView;
        readSummaryData = new ReadSummaryData();
    }

    public void getReadSummary(String bookName, String pageNumber) {
        final RequestReadSummaryQuery req = new RequestReadSummaryQuery(bookName, pageNumber);
        readSummaryData.getReadSummary(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                readSummaryView.setSummary(req.getReadSummaryEntity().summary);
                readSummaryView.setNewWordList(readSummaryData.getNewWordReviewBeenList());
                readSummaryView.setGoodSentenceList(readSummaryData.getReadSummaryGoodSentenceReviewBeen());
            }
        });
    }

    public void saveReadSummary(String bookName, String pageNumber, String summary, String newWordListJson, String goodSentenceJson) {

        ReadSummaryEntity readSummaryEntity = new ReadSummaryEntity();
        readSummaryEntity.bookName = bookName;
        readSummaryEntity.pageNumber = pageNumber;
        readSummaryEntity.summary = summary;
        readSummaryEntity.newWordList = newWordListJson;
        readSummaryEntity.goodSentenceList = goodSentenceJson;
        RequestReadSummaryInsert req = new RequestReadSummaryInsert(readSummaryEntity);
        readSummaryData.saveReadSummary(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance().getResources().getString(R.string.notation_add_fail));
                }
            }
        });
    }
}
