package com.onyx.android.sdk.data.model;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.onyx.android.sdk.data.Constant;

import java.io.Serializable;

/**
 * Created by suicheng on 2017/1/22.
 */
public class WeChatOauthResp implements Serializable {

    @JSONField(name = "access_token")
    public String accessToken;
    @JSONField(name = "openid")
    public String openId;
    @JSONField(name = "refresh_token")
    public String refreshToken;
    @JSONField(name = "expires_in")
    public String expiresIn;
    public String scope;
    @JSONField(name = "unionid")
    public String unionId;
}
