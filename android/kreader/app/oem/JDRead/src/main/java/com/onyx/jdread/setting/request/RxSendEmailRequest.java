package com.onyx.jdread.setting.request;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.HtmlEmail;

/**
 * Created by hehai on 18-1-10.
 */

public class RxSendEmailRequest extends RxBaseCloudRequest {
    private String host;
    private int port;
    private String emailAddress;
    private String password;
    private String subject;
    private String message;
    private String attachMentPath;

    public RxSendEmailRequest(String host, int port, String emailAddress, String password, String subject, String message, String attachMentPath) {
        this.host = host;
        this.port = port;
        this.emailAddress = emailAddress;
        this.password = password;
        this.subject = subject;
        this.message = message;
        this.attachMentPath = attachMentPath;
    }

    @Override
    public RxSendEmailRequest call() throws Exception {
        HtmlEmail htmlEmail = new HtmlEmail();
        htmlEmail.setHostName(host);
        htmlEmail.setSmtpPort(port);
        htmlEmail.setTLS(true);
        htmlEmail.setSSL(true);
        htmlEmail.setCharset("gbk");
        htmlEmail.addTo(emailAddress);
        htmlEmail.setFrom(emailAddress);
        htmlEmail.setAuthentication(emailAddress, password);
        htmlEmail.setSubject(subject);
        if (StringUtils.isNotBlank(attachMentPath) && FileUtils.fileExist(attachMentPath)) {
            EmailAttachment attachment = new EmailAttachment();
            attachment.setPath(attachMentPath);
            attachment.setDisposition(EmailAttachment.ATTACHMENT);
            htmlEmail.attach(attachment);
        }
        htmlEmail.setMsg(message);
        htmlEmail.send();
        return this;
    }
}
