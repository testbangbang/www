package com.onyx.jdread.setting.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.request.RxFirmwareLocalUpdateRequest;

/**
 * Created by li on 2017/12/25.
 */

public class LocalUpdateSystemAction extends BaseAction {
    @Override
    public void execute(SettingBundle bundle, RxCallback callback) {
        RxFirmwareLocalUpdateRequest rq = new RxFirmwareLocalUpdateRequest();
        rq.execute(callback);
    }
}
