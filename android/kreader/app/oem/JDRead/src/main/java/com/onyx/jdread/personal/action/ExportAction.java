package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.personal.cloud.entity.jdbean.NoteBean;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.request.local.RxRequestExportNotes;
import com.onyx.jdread.setting.common.ExportHelper;

import java.util.List;

/**
 * Created by li on 2018/3/8.
 */

public class ExportAction extends BaseAction {
    private int exportType;
    private List<NoteBean> data;
    private ExportHelper helper;

    public ExportAction(ExportHelper helper, int exportType, List<NoteBean> data) {
        this.helper = helper;
        this.exportType = exportType;
        this.data = data;
    }

    @Override
    public void execute(PersonalDataBundle dataBundle, RxCallback rxCallback) {
        RxRequestExportNotes rq = new RxRequestExportNotes(helper, exportType, data);
        rq.execute(rxCallback);
    }
}
