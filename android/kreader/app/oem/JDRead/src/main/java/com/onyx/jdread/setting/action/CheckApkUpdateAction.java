package com.onyx.jdread.setting.action;

import com.onyx.android.sdk.data.model.ApplicationUpdate;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.request.RxCheckApkUpdateRequest;
import com.onyx.jdread.setting.utils.UpdateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li on 2017/12/26.
 */

public class CheckApkUpdateAction extends BaseAction {
    @Override
    public void execute(final SettingBundle bundle, final RxCallback callback) {
        ApplicationUpdate queryAppUpdate = UpdateUtil.getQueryAppUpdate();
        List<ApplicationUpdate> list = new ArrayList<>();
        list.add(queryAppUpdate);
        final RxCheckApkUpdateRequest rq = new RxCheckApkUpdateRequest(bundle.getCloudManager(), list);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ApplicationUpdate applicationUpdate = rq.getApplicationUpdate();
                bundle.setApplicationUpdate(applicationUpdate);
                if (callback != null) {
                    callback.onNext(CheckApkUpdateAction.class);
                }
            }
        });
    }
}
