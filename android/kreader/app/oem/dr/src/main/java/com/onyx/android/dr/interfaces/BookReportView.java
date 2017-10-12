package com.onyx.android.dr.interfaces;

import com.onyx.android.sdk.data.model.v2.CreateBookReportResult;
import com.onyx.android.sdk.data.model.v2.GetBookReportListBean;

import java.util.List;

/**
 * Created by li on 2017/9/19.
 */

public interface BookReportView {
    void setBookReportList(List<GetBookReportListBean> list);

    void setDeleteResult();

    void getBookReport(CreateBookReportResult result);

    void addCommentResult(CreateBookReportResult result);

    void setLibraryId(String bookId, String libraryId);

    void saveBookReportData(CreateBookReportResult createBookReportResult);
}
