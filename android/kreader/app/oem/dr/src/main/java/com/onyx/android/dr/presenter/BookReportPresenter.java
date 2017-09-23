package com.onyx.android.dr.presenter;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.common.CommonNotices;
import com.onyx.android.dr.data.BookReportData;
import com.onyx.android.dr.interfaces.BookReportView;
import com.onyx.android.dr.request.cloud.BringOutBookReportRequest;
import com.onyx.android.dr.request.cloud.CreateBookReportRequest;
import com.onyx.android.dr.request.cloud.DeleteBookReportRequest;
import com.onyx.android.dr.request.cloud.GetBookReportListRequest;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.v2.CreateBookReportRequestBean;
import com.onyx.android.sdk.data.model.v2.CreateBookReportResult;
import com.onyx.android.sdk.data.model.v2.GetBookReportList;
import com.onyx.android.sdk.data.model.v2.GetBookReportListBean;
import com.onyx.android.sdk.data.model.v2.GetBookReportListRequestBean;

import java.util.ArrayList;
import java.util.List;

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
                if (bookReportList != null && bookReportList.list != null && bookReportList.list.size() > 0) {
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

    public void bringOutReport(GetBookReportListBean bookReportBean) {
        List<String> titleList = new ArrayList<>();
        titleList.add(DRApplication.getInstance().getResources().getString(R.string.book_report_list_time));
        titleList.add(DRApplication.getInstance().getResources().getString(R.string.book_report_list_book_name));
        titleList.add(DRApplication.getInstance().getResources().getString(R.string.book_report_list_pages));
        titleList.add(DRApplication.getInstance().getResources().getString(R.string.book_report_list_summary));
        titleList.add(DRApplication.getInstance().getResources().getString(R.string.book_report_list_word_count));

        String title = DRApplication.getInstance().getResources().getString(R.string.reader_response);
        final BringOutBookReportRequest rq = new BringOutBookReportRequest(title, titleList, bookReportBean);
        bookReportData.bringOutReport(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance()
                            .getResources().getString(R.string.export_success));
                } else {
                    CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance()
                            .getResources().getString(R.string.export_failed));
                }
            }
        });
    }

    public void createImpression(String bookId, String bookName, String content, String currentPage) {
        CreateBookReportRequestBean requestBean = new CreateBookReportRequestBean();
        requestBean.setName(bookName);
        requestBean.setBook(bookId);
        requestBean.setContent(content);
        final CreateBookReportRequest rq = new CreateBookReportRequest(requestBean);
        bookReportData.createImpression(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                CreateBookReportResult createBookReportResult = rq.getCreateBookReportResult();
                if(createBookReportResult != null) {
                    CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance()
                            .getResources().getString(R.string.saved_successfully));
                    bookReportView.setCreateBookReportData(createBookReportResult);
                }else {
                    CommonNotices.showMessage(DRApplication.getInstance(), DRApplication.getInstance()
                            .getResources().getString(R.string.save_failed));
                }
            }
        });
    }
}
