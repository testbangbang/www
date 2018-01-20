package com.onyx.jdread.setting.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.request.RxSaveBootPicRequest;
import com.onyx.jdread.setting.utils.Constants;

/**
 * Created by hehai on 18-1-1.
 */

public class SaveBootPicAction extends BaseAction<SettingBundle> {
    private String srcPath;

    public SaveBootPicAction(String srcPath) {
        this.srcPath = srcPath;
    }

    @Override
    public void execute(final SettingBundle bundle, final RxCallback callback) {
        final String standbyPath = Constants.STANDBY_PIC_DIRECTORY + Constants.STANDBY_PIC_NAME;
        RxSaveBootPicRequest rxSaveBootPicRequest = new RxSaveBootPicRequest(SettingBundle.getInstance().getDataManager(), srcPath, standbyPath);
        rxSaveBootPicRequest.execute(new RxCallback<RxSaveBootPicRequest>() {
            @Override
            public void onNext(RxSaveBootPicRequest request) {
                JDPreferenceManager.setStringValue(R.string.screen_saver_key, srcPath);
                hideLoadingDialog(bundle);
                if (callback != null) {
                    callback.onNext(request);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                hideLoadingDialog(bundle);
                if (callback != null) {
                    callback.onError(throwable);
                }
            }
        });
        showLoadingDialog(bundle, R.string.being_set_up);
    }
}
