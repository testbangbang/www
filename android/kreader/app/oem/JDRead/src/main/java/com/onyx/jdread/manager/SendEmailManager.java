package com.onyx.jdread.manager;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.setting.action.SendEmailAction;
import com.onyx.jdread.setting.model.SettingBundle;

/**
 * Created by hehai on 18-1-10.
 */

public class SendEmailManager {

    public static void init(final String host, final int port, final String emailAddress, final String password) {
        SendEmailAction sendEmailAction = new SendEmailAction(host, port, emailAddress, password, JDReadApplication.getInstance().getString(R.string.bind_to_email),
                JDReadApplication.getInstance().getString(R.string.bind_to_email), null);
        sendEmailAction.execute(SettingBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                PreferenceManager.setStringValue(JDReadApplication.getInstance(), R.string.email_host_key, host);
                PreferenceManager.setIntValue(JDReadApplication.getInstance(), R.string.email_port_key, port);
                PreferenceManager.setStringValue(JDReadApplication.getInstance(), R.string.email_address_key, emailAddress);
                PreferenceManager.setStringValue(JDReadApplication.getInstance(), R.string.email_psw_key, password);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                ToastUtil.showToast(JDReadApplication.getInstance().getString(R.string.bind_to_email_failed));
            }
        });
    }

    public static void sendMessage(String subject, String message, String attachmentPath) {
        SendEmailAction emailAction = new SendEmailAction(subject, message, attachmentPath);
        emailAction.execute(SettingBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                ToastUtil.showToast(JDReadApplication.getInstance().getString(R.string.send_succeed));
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                ToastUtil.showToast(JDReadApplication.getInstance().getString(R.string.bind_to_email_failed));
            }
        });
    }
}
