package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.data.BookReportData;
import com.onyx.android.dr.data.MyNotesTypeConfig;
import com.onyx.android.dr.data.ReadingRateData;
import com.onyx.android.dr.interfaces.MyNotesView;
import com.onyx.android.dr.request.cloud.GetBookReportListRequest;
import com.onyx.android.dr.request.cloud.GetSharedImpressionRequest;
import com.onyx.android.dr.request.local.ReadingRateQueryAll;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.v2.GetBookReportList;
import com.onyx.android.sdk.data.model.v2.GetBookReportListBean;
import com.onyx.android.sdk.data.model.v2.GetBookReportListRequestBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class MyNotesPresenter {
    private final MyNotesTypeConfig myNotesTypeConfig;
    private final ReadingRateData readingRateData;
    private final BookReportData bookReportData;
    private MyNotesView myNotesView;
    private Context context;
    private String offset = "1";
    private String limit = "4";
    private String sortBy = "createdAt";
    private String order = "1";
    private List<GetBookReportListBean> data;
    private List<Boolean> listCheck;

    public MyNotesPresenter(Context context, MyNotesView myNotesView) {
        this.myNotesView = myNotesView;
        this.context = context;
        myNotesTypeConfig = new MyNotesTypeConfig();
        readingRateData = new ReadingRateData();
        bookReportData = new BookReportData();
    }

    public void getImpressionsList() {
        GetBookReportListRequestBean requestBean = new GetBookReportListRequestBean();
        requestBean.offset = offset;
        requestBean.limit = limit;
        requestBean.order = order;
        requestBean.sortBy = sortBy;
        final GetBookReportListRequest rq = new GetBookReportListRequest(requestBean);
        final String libraryId = DRPreferenceManager.loadLibraryParentId(DRApplication.getInstance(), "");
        bookReportData.getImpressionsList(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GetBookReportList bookReportList = rq.getBookReportList();
                ArrayList<Boolean> checkList = rq.getCheckList();
                if (data == null) {
                    data = new ArrayList<>();
                    listCheck = new ArrayList<>();
                } else {
                    data.clear();
                    listCheck.clear();
                }
                if (bookReportList != null && bookReportList.list != null && bookReportList.list.size() > 0) {
                    data.addAll(bookReportList.list);
                    listCheck.addAll(checkList);
                }
                getSharedImpressions(libraryId);
            }
        });
    }

    public void getSharedImpressions(String libraryId) {
        GetBookReportListRequestBean requestBean = new GetBookReportListRequestBean();
        requestBean.offset = offset;
        requestBean.limit = limit;
        requestBean.order = order;
        requestBean.sortBy = sortBy;
        final GetSharedImpressionRequest rq = new GetSharedImpressionRequest(libraryId, requestBean);
        bookReportData.getSharedImpressions(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }

    public void getAllReadingRateData() {
        final ReadingRateQueryAll req = new ReadingRateQueryAll(readingRateData);
        readingRateData.getAllReadingRate(context, req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }

    public void loadData(Context context) {
        myNotesTypeConfig.loadDictInfo(context);
    }

    public void loadMyTracks(int userType) {
        myNotesView.setMyracksData(myNotesTypeConfig.getMenuData(userType));
    }

    public void loadMyThink(int userType) {
        myNotesView.setMyThinkData(myNotesTypeConfig.getMenuData(userType));
    }

    public void loadMyCreation(int userType) {
        myNotesView.setMyCreationData(myNotesTypeConfig.getMenuData(userType));
    }
}
