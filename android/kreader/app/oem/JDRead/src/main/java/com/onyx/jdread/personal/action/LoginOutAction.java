package com.onyx.jdread.personal.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.databinding.PersonalBinding;
import com.onyx.jdread.main.common.ClientUtils;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.personal.common.LoginHelper;
import com.onyx.jdread.personal.event.RequestFailedEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;

import jd.wjlogin_sdk.common.WJLoginHelper;
import jd.wjlogin_sdk.common.listener.OnCommonCallback;
import jd.wjlogin_sdk.model.FailResult;

/**
 * Created by li on 2018/2/25.
 */

public class LoginOutAction extends BaseAction {
    private PersonalBinding binding;

    public LoginOutAction(PersonalBinding binding) {
        this.binding = binding;
    }

    @Override
    public void execute(final PersonalDataBundle dataBundle, RxCallback rxCallback) {
        WJLoginHelper helper = ClientUtils.getWJLoginHelper();
        helper.exitLogin(new OnCommonCallback() {
            @Override
            public void onSuccess() {
                binding.setIsLogin(false);
                JDReadApplication.getInstance().setLogin(false);
                LoginHelper.clearUserInfo();
            }

            @Override
            public void onError(String s) {
                if (StringUtils.isNotBlank(s)) {
                    dataBundle.getEventBus().post(new RequestFailedEvent(s));
                }
            }

            @Override
            public void onFail(FailResult failResult) {
                String message = failResult.getMessage();
                if (StringUtils.isNotBlank(message)) {
                    dataBundle.getEventBus().post(new RequestFailedEvent(message));
                }
            }
        });
    }
}
