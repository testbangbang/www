package com.onyx.android.dr.request.cloud;

import com.onyx.android.dr.util.ExportToHtmlUtils;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.v2.GetBookReportListBean;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;

import java.util.List;

/**
 * Created by li on 2017/9/22.
 */

public class BringOutBookReportRequest extends BaseCloudRequest {
    private String title;
    private List<GetBookReportListBean> list;
    private List<String> titleList;

    public BringOutBookReportRequest(String title, List<String> titleList, List<GetBookReportListBean> list) {
        this.title = title;
        this.titleList = titleList;
        this.list = list;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        ExportToHtmlUtils.exportBookReportToHtml(title, titleList, list);
    }
}
