package com.onyx.jdread.personal.request.local;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.jdread.personal.cloud.entity.jdbean.NoteBean;
import com.onyx.jdread.setting.common.ExportHelper;

import java.util.List;

/**
 * Created by li on 2018/3/8.
 */

public class RxRequestExportNotes extends RxBaseCloudRequest {
    private int exportType;
    private List<NoteBean> data;
    private ExportHelper helper;

    public RxRequestExportNotes(ExportHelper helper, int exportType, List<NoteBean> data) {
        this.helper = helper;
        this.exportType = exportType;
        this.data = data;
    }

    @Override
    public Object call() throws Exception {
        helper.exportNote(exportType, data);
        return this;
    }
}
