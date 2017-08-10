package com.onyx.android.dr.presenter;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.data.RecordTimeConfig;
import com.onyx.android.dr.interfaces.RecordTimeSettingView;

/**
 * Created by zhouzhiming on 2017/8/9.
 */
public class RecordTimeSettingPresenter {
    private final RecordTimeConfig recordTimeConfig;
    private RecordTimeSettingView recordTimeSettingView;

    public RecordTimeSettingPresenter(RecordTimeSettingView recordTimeSettingView) {
        this.recordTimeSettingView = recordTimeSettingView;
        recordTimeConfig = new RecordTimeConfig();
    }

    public void getRecordTimeData() {
        recordTimeSettingView.setRecordTime(recordTimeConfig.loadRecordTimeData(DRApplication.getInstance()));
    }
}
