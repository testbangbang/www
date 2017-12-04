package com.onyx.android.dr.interfaces;

import com.onyx.android.sdk.data.model.CreateInformalEssayBean;
import com.onyx.android.sdk.data.model.CreateInformalSecondBean;
import com.onyx.android.sdk.data.model.v2.CreateBookReportResult;
import com.onyx.android.sdk.data.model.v2.GetBookReportListBean;

import java.util.List;

/**
 * Created by li on 2017/9/19.
 */

public interface BookReportView {
    void setBookReportList(List<GetBookReportListBean> list, List<Boolean> listCheck);

    void setDeleteResult();

    void getBookReport(CreateBookReportResult result);

    void addCommentResult(CreateBookReportResult result);

    void addInformalCommentResult(CreateInformalSecondBean result);

    void setLibraryId(String bookId, String libraryId);

    void saveBookReportData(CreateBookReportResult createBookReportResult);

    void setInformalEssayData(List<CreateInformalEssayBean> dataList, List<Boolean> listCheck);

    void createInformalEssay(boolean tag);
}
