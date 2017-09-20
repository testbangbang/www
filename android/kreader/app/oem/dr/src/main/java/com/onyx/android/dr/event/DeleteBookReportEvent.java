package com.onyx.android.dr.event;

import com.onyx.android.sdk.data.model.v2.GetBookReportListBean;

/**
 * Created by li on 2017/9/20.
 */

public class DeleteBookReportEvent {
    private GetBookReportListBean bookReportBean;

    public DeleteBookReportEvent(GetBookReportListBean bookReportBean) {
        this.bookReportBean = bookReportBean;
    }

    public GetBookReportListBean getBookReportBean() {
        return bookReportBean;
    }
}
