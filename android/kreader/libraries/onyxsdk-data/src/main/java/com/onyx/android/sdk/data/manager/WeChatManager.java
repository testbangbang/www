package com.onyx.android.sdk.data.manager;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.WeChatUserInfo;
import com.onyx.android.sdk.data.request.cloud.AccountGetOAuthCodeRequest;
import com.onyx.android.sdk.data.request.cloud.WeChatOauthRequest;
import com.onyx.android.sdk.data.request.cloud.WeChatUserInfoRequest;
import com.onyx.android.sdk.utils.StringUtils;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by wang.suicheng on 2017/1/22.
 */
public class WeChatManager {

    public static final String WE_CHAT_LOGIN_OAUTH_ACTION = "we_chat_login_oauth_action";

    private static String WX_APP_ID = "";
    private static String WX_APP_Secret = "";

    private static WeChatManager globalInstance;
    private IWXAPI weChatApi;
    private String respCode;

    private WeChatManager(Context context) {
        initWeChatApi(context);
    }

    private void initWeChatApi(Context context) {
        weChatApi = WXAPIFactory.createWXAPI(context, WX_APP_ID, true);
        weChatApi.registerApp(WX_APP_ID);
    }

    static public WeChatManager sharedInstance(Context context) {
        if (globalInstance == null) {
            globalInstance = new WeChatManager(context);
        }
        return globalInstance;
    }

    //must first init
    static public WeChatManager sharedInstance(Context context, String appId, String AppSecret) {
        if (globalInstance == null) {
            WX_APP_ID = appId;
            WX_APP_Secret = AppSecret;
            globalInstance = sharedInstance(context);
        }
        return globalInstance;
    }

    public IWXAPI getWeChatApi() {
        return weChatApi;
    }

    public void sendWeChatAuthRequest(Context context) {
        if (weChatApi == null) {
            initWeChatApi(context);
        }
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wx_login_onyx";
        weChatApi.sendReq(req);
    }

    public boolean sendWeChatTokenRequest(CloudStore cloudStore, Context context, BaseCallback callback) {
        if (StringUtils.isNullOrEmpty(respCode)) {
            return false;
        }
        WeChatOauthRequest request = new WeChatOauthRequest(WX_APP_ID, WX_APP_Secret, respCode);
        cloudStore.submitRequest(context, request, callback);
        return true;
    }

    public boolean sendWeChatUserInfoRequest(CloudStore cloudStore, Context context, String token, String openId, BaseCallback callback) {
        if (StringUtils.isNullOrEmpty(token) || StringUtils.isNullOrEmpty(openId)) {
            return false;
        }
        WeChatUserInfoRequest request = new WeChatUserInfoRequest(token, openId);
        cloudStore.submitRequest(context, request, callback);
        return true;
    }

    public boolean sendOnyxOAuthCodeRequest(CloudStore cloudStore, Context context, WeChatUserInfo userInfo, BaseCallback callback) {
        final AccountGetOAuthCodeRequest getOAuthCodeRequest = new AccountGetOAuthCodeRequest(Constant.PLATFORM_WECHAT, userInfo.createOAuthAccountData());
        cloudStore.submitRequest(context, getOAuthCodeRequest, callback);
        return true;
    }

    public String getRespCode() {
        return respCode;
    }

    public String getRespCodeAndReset() {
        final String returnCode = respCode;
        respCode = null;
        return returnCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getWxAppId() {
        return WX_APP_ID;
    }

    public String getWxAppSecret() {
        return WX_APP_Secret;
    }
}
