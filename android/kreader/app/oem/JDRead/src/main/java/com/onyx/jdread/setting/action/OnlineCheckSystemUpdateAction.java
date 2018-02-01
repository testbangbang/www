package com.onyx.jdread.setting.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.request.RxFirmwareUpdateRequest;
import com.onyx.jdread.setting.utils.UpdateUtil;

/**
 * Created by li on 2017/12/25.
 */

public class OnlineCheckSystemUpdateAction extends BaseAction {
    @Override
    public void execute(final SettingBundle bundle, final RxCallback callback) {
        final RxFirmwareUpdateRequest rq = UpdateUtil.cloudFirmwareCheckRequest(JDReadApplication.getInstance(), bundle.getCloudManager());
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                bundle.setFirmwareValid(rq.isResultFirmwareValid());
                bundle.setResultFirmware(rq.getResultFirmware());
                RxCallback.invokeNext(callback, OnlineCheckSystemUpdateAction.this);
            }

            @Override
            public void onError(Throwable e) {
                RxCallback.invokeError(callback, e);
            }
        });
    }
}
