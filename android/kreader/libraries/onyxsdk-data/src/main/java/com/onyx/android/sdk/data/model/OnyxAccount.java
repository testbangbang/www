package com.onyx.android.sdk.data.model;

import android.content.Context;
import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by solskjaer49 on 15/5/6 17:34.
 */
public class OnyxAccount extends BaseData {


    public String fullName;
    public String userName;
    public String password;
    public String email;
    public String mobile;

    public String sessionToken;

    public String captchaId;
    public String captchaAnswer;
    public boolean isInstallationId = false;
    public String deviceClient = "boox";
    public String type;

    public OnyxAccount() {
    }

    public OnyxAccount(String fullName, String password, String email) {
        this.fullName = fullName;
        this.password = password;
        this.email = email;
    }

}
