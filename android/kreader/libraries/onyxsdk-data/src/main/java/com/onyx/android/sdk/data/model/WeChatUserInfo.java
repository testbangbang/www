package com.onyx.android.sdk.data.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by suicheng on 2017/1/22.
 */
public class WeChatUserInfo implements Serializable {

    @JSONField(name = "nickname")
    public String nickName;
    public String sex;
    public String province;
    public String city;
    public String country;
    @JSONField(name = "headimgurl")
    public String headImgUrl;
    public String privilege;
    @JSONField(name = "unionid")
    public String unionId;

    public OAuthAccountData createOAuthAccountData() {
        OAuthAccountData data = new OAuthAccountData();
        data.oAuthName = nickName;
        data.oAuthId = unionId;
        data.avatarUrl = headImgUrl;
        return data;
    }
}
