package com.onyx.android.sdk.data.model;

import com.onyx.android.sdk.data.Constant;

import java.io.Serializable;

/**
 * Created by suicheng on 2017/2/6.
 */
public class OAuthAccountData implements Serializable {

    public String appId = Constant.APP_ID;
    public String oAuthId;
    public String oAuthName;
    public String avatarUrl;
}
