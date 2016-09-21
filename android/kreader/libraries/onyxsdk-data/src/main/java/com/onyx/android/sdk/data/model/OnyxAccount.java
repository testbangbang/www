package com.onyx.android.sdk.data.model;

/**
 * Created by solskjaer49 on 15/5/6 17:34.
 */
public class OnyxAccount extends BaseData {

    public String nickName;
    public String firstName;
    public String lastName;
    public String fullName;
    public String password;
    public String email;
    public String mobile;

    public String sessionToken;

    public String captchaId;
    public String captchaValue;
    public boolean isInstallationId = false;
    public String deviceClient = "boox";
    public String type;
    public String avatarUrl;
    public int status;

    public OnyxAccount() {
    }

    public OnyxAccount(String fullName, String password, String email) {
        this.fullName = fullName;
        this.password = password;
        this.email = email;
    }

}
