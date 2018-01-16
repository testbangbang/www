package com.onyx.jdread.setting.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.manager.KSICibaTranslate;
import com.onyx.jdread.setting.model.SettingBundle;

/**
 * Created by hehai on 18-1-16.
 */

public class TranslateAction extends BaseAction<SettingBundle> {
    private String inputWords;
    private String translateResult;

    public TranslateAction(String inputWords) {
        this.inputWords = inputWords;
    }

    public String getTranslateResult() {
        return translateResult;
    }

    @Override
    public void execute(SettingBundle bundle, final RxCallback callback) {
        KSICibaTranslate.getInstance().getTranslateResult(inputWords, new KSICibaTranslate.OnyxIKSCibaQueryResult(new KSICibaTranslate.OnyxIKSCibaQueryResult.TranslateResult() {
            @Override
            public void translateResult(String result) {
                translateResult = result;
                callback.onNext(result);
            }
        }));
    }
}
