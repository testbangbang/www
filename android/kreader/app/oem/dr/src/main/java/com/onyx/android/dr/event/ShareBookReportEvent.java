package com.onyx.android.dr.event;

import com.onyx.android.sdk.data.model.v2.GetBookReportListBean;

/**
 * Created by li on 2017/9/27.
 */

public class ShareBookReportEvent {
    private GetBookReportListBean bookReportBean;

    public ShareBookReportEvent(GetBookReportListBean bookReportBean) {
        this.bookReportBean = bookReportBean;
    }

    public GetBookReportListBean getBookReportBean() {
        return bookReportBean;
    }
}
