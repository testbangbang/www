package com.onyx.jdread.setting.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.request.RxSaveBootPicRequest;

import java.util.List;

/**
 * Created by hehai on 18-1-1.
 */

public class SaveBootPicAction extends BaseAction<SettingBundle> {
    private String display;
    private List<String> pics;

    public SaveBootPicAction(String display, List<String> pics) {
        this.display = display;
        this.pics = pics;
    }

    @Override
    public void execute(final SettingBundle bundle, final RxCallback callback) {
        RxSaveBootPicRequest rxSaveBootPicRequest = new RxSaveBootPicRequest(SettingBundle.getInstance().getDataManager(), pics);
        rxSaveBootPicRequest.execute(new RxCallback<RxSaveBootPicRequest>() {
            @Override
            public void onNext(RxSaveBootPicRequest request) {
                JDPreferenceManager.setStringValue(R.string.screen_saver_key, display);
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
