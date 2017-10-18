package com.onyx.android.dr.request.local;

import com.onyx.android.dr.bean.AnnotationStatisticsBean;
import com.onyx.android.dr.util.ExportToHtmlUtils;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;

import java.util.List;

/**
 * Created by li on 2017/9/22.
 */

public class ExportAnnotationListRequest extends BaseCloudRequest {
    private String title;
    private List<AnnotationStatisticsBean> list;
    private List<String> titleList;

    public ExportAnnotationListRequest(String title, List<String> titleList, List<AnnotationStatisticsBean> list) {
        this.title = title;
        this.titleList = titleList;
        this.list = list;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        ExportToHtmlUtils.exportAnnotationDataToHtml(title, titleList, list);
    }
}
