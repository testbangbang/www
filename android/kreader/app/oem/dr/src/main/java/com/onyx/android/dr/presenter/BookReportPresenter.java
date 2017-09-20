package com.onyx.android.dr.presenter;

import com.onyx.android.dr.data.BookReportData;
import com.onyx.android.dr.interfaces.BookReportView;
import com.onyx.android.dr.request.cloud.DeleteBookReportRequest;
import com.onyx.android.dr.request.cloud.GetBookReportListRequest;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.v2.GetBookReportList;
import com.onyx.android.sdk.data.model.v2.GetBookReportListRequestBean;

/**
 * Created by li on 2017/9/19.
 */

public class BookReportPresenter {
    private BookReportView bookReportView;
    private BookReportData bookReportData;

    public BookReportPresenter(BookReportView bookReportView) {
        this.bookReportView = bookReportView;
        bookReportData = new BookReportData();
    }

    public void getImpressionsList() {
        GetBookReportListRequestBean requstBean = new GetBookReportListRequestBean();
        requstBean.offset = "1";
        requstBean.limit = "4";
        requstBean.order = "1";
        requstBean.sortBy = "createdAt";

        final GetBookReportListRequest rq = new GetBookReportListRequest(requstBean);
        bookReportData.getImpressionsList(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GetBookReportList bookReportList = rq.getBookReportList();
                if(bookReportList != null && bookReportList.list != null && bookReportList.list.size() > 0) {
                    bookReportView.setBookReportList(bookReportList.list);
                }
            }
        });
    }

    public void deleteImpression(String id) {
        final DeleteBookReportRequest rq = new DeleteBookReportRequest(id);
        bookReportData.deleteImpression(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                bookReportView.setDeleteResult();
            }
        });
    }
}
