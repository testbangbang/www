package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.data.BookReportData;
import com.onyx.android.dr.data.MyNotesTypeConfig;
import com.onyx.android.dr.interfaces.MyNotesView;
import com.onyx.android.dr.request.cloud.GetBookReportListRequest;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.v2.GetBookReportListRequestBean;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class MyNotesPresenter {
    private final MyNotesTypeConfig myNotesTypeConfig;
    private MyNotesView myNotesView;
    private Context context;
    private String offset = "1";
    private String limit = "4";
    private String sortBy = "createdAt";
    private String order = "1";

    public MyNotesPresenter(Context context, MyNotesView myNotesView) {
        this.myNotesView = myNotesView;
        this.context = context;
        myNotesTypeConfig = new MyNotesTypeConfig();
    }

    public void getImpressionsList() {
        BookReportData bookReportData = new BookReportData();
        GetBookReportListRequestBean requestBean = new GetBookReportListRequestBean();
        requestBean.offset = offset;
        requestBean.limit = limit;
        requestBean.order = order;
        requestBean.sortBy = sortBy;
        final GetBookReportListRequest rq = new GetBookReportListRequest(requestBean);
        bookReportData.getImpressionsList(rq, new BaseCallback() {
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
