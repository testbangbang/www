package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.personal.common.EncryptHelper;
import com.onyx.jdread.personal.event.PersonalErrorEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;

/**
 * Created by li on 2018/1/31.
 */

public class AutoLoginAction extends BaseAction {
    @Override
    public void execute(final PersonalDataBundle dataBundle, RxCallback rxCallback) {
        if (StringUtils.isNullOrEmpty(dataBundle.getSalt())) {
            EncryptHelper.getSaltValue(dataBundle, new RxCallback() {
                @Override
                public void onNext(Object o) {
                    login();
                }

                @Override
                public void onError(Throwable throwable) {
                    super.onError(throwable);
                    PersonalErrorEvent.onErrorHandle(throwable, getClass().getSimpleName(), dataBundle.getEventBus());
                }
            });
        } else {
            login();
        }
    }

    private void login() {
        String account = JDPreferenceManager.getStringValue(Constants.SP_KEY_ACCOUNT, "");
        String password = JDPreferenceManager.getStringValue(Constants.SP_KEY_PASSWORD, "");
        if (StringUtils.isNullOrEmpty(account) || StringUtils.isNullOrEmpty(password) || JDReadApplication.getInstance().getLogin()) {
            return;
        }
        UserLoginAction action = new UserLoginAction(JDReadApplication.getInstance(), account, password,true);
        action.execute(PersonalDataBundle.getInstance(), null);
    }
}
