package com.onyx.jdread.setting.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.request.RxSendEmailRequest;

/**
 * Created by hehai on 18-1-10.
 */

public class SendEmailAction extends BaseAction<SettingBundle> {
    private String host;
    private int port;
    private String emailAddress;
    private String password;
    private String subject;
    private String message;
    private String attachmentPath;

    public SendEmailAction( String subject, String message, String attachmentPath) {
        this.subject = subject;
        this.message = message;
        this.attachmentPath = attachmentPath;
    }

    public SendEmailAction(String host, int port, String emailAddress, String password, String subject, String message, String attachmentPath) {
        this.host = host;
        this.port = port;
        this.emailAddress = emailAddress;
        this.password = password;
        this.subject = subject;
        this.message = message;
        this.attachmentPath = attachmentPath;
    }

    @Override
    public void execute(SettingBundle bundle, RxCallback callback) {
        host = StringUtils.isNullOrEmpty(host) ? PreferenceManager.getStringValue(JDReadApplication.getInstance(), R.string.email_host_key, null) : host;
        port = port == 0 ? PreferenceManager.getIntValue(JDReadApplication.getInstance(), R.string.email_port_key, 0) : port;
        emailAddress = StringUtils.isNullOrEmpty(emailAddress) ? PreferenceManager.getStringValue(JDReadApplication.getInstance(), R.string.email_address_key, null) : emailAddress;
        password = StringUtils.isNullOrEmpty(password) ? PreferenceManager.getStringValue(JDReadApplication.getInstance(), R.string.email_psw_key, null) : password;
        RxSendEmailRequest rxSendEmailRequest = new RxSendEmailRequest(host, port, emailAddress, password, subject, message, attachmentPath);
        rxSendEmailRequest.execute(callback);
    }
}
