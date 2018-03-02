package com.onyx.jdread.setting.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.setting.model.ScreenSaversModel;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.request.RxLoadPicByPathRequest;
import com.onyx.jdread.setting.utils.Constants;

/**
 * Created by hehai on 18-1-1.
 */

public class LoadScreenSaversPicAction extends BaseAction<SettingBundle> {
    private ScreenSaversModel screenSaversModel;

    public LoadScreenSaversPicAction(ScreenSaversModel screenSaversModel) {
        this.screenSaversModel = screenSaversModel;
    }

    @Override
    public void execute(SettingBundle bundle, final RxCallback callback) {
        RxLoadPicByPathRequest request = new RxLoadPicByPathRequest(bundle.getDataManager(), Constants.SYSTEM_MEADIA);
        request.execute(new RxCallback<RxLoadPicByPathRequest>() {
            @Override
            public void onNext(RxLoadPicByPathRequest picByPathRequest) {
                screenSaversModel.picList.clear();
                screenSaversModel.picList.addAll(picByPathRequest.getPics());
                if (callback != null) {
                    callback.onNext(picByPathRequest);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (callback != null) {
                    callback.onError(throwable);
                }
            }
        });
    }
}
