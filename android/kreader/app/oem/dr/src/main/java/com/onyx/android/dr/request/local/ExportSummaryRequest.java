package com.onyx.android.dr.request.local;

import com.onyx.android.dr.reader.data.ReadSummaryEntity;
import com.onyx.android.dr.util.ExportToHtmlUtils;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;

import java.util.List;

/**
 * Created by li on 2017/9/22.
 */

public class ExportSummaryRequest extends BaseCloudRequest {
    private String title;
    private List<ReadSummaryEntity> list;
    private List<String> titleList;

    public ExportSummaryRequest(String title, List<String> titleList, List<ReadSummaryEntity> list) {
        this.title = title;
        this.titleList = titleList;
        this.list = list;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        ExportToHtmlUtils.exportSummaryDataToHtml(title, titleList, list);
    }
}
