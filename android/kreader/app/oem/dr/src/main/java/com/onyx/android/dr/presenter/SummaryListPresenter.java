package com.onyx.android.dr.presenter;

import com.onyx.android.dr.data.SummaryListData;
import com.onyx.android.dr.interfaces.SummaryView;
import com.onyx.android.dr.reader.data.ReadSummaryEntity;
import com.onyx.android.dr.request.local.RequestSummaryDelete;
import com.onyx.android.dr.request.local.RequestSummaryQueryAll;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

import java.util.List;

/**
 * Created by hehai on 17-9-20.
 */

public class SummaryListPresenter {
    private SummaryView summaryView;
    private SummaryListData summaryListData;

    public SummaryListPresenter(SummaryView summaryView) {
        this.summaryView = summaryView;
        summaryListData = new SummaryListData();
    }

    public void getSummaryList() {
        final RequestSummaryQueryAll req = new RequestSummaryQueryAll();
        summaryListData.getSummaryList(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                summaryView.setSummaryList(req.getSummaryList());
            }
        });
    }

    public void removeSummary(List<ReadSummaryEntity> selectedList) {
        RequestSummaryDelete req = new RequestSummaryDelete(selectedList);
        summaryListData.removeSummary(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                getSummaryList();
            }
        });
    }
}
