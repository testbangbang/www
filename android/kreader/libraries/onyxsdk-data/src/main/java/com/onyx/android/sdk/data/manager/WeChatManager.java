package com.onyx.android.sdk.data.manager;

import android.content.Context;
import android.graphics.Bitmap;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.CloudStore;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.WeChatUserInfo;
import com.onyx.android.sdk.data.request.cloud.AccountGetOAuthCodeRequest;
import com.onyx.android.sdk.data.request.cloud.WeChatOauthRequest;
import com.onyx.android.sdk.data.request.cloud.WeChatUserInfoRequest;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMusicObject;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXVideoObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
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

    public void shareText(MediaBuilder mediaBuilder) {
        WXTextObject textObj = new WXTextObject();
        textObj.text = mediaBuilder.getContent();
        WXMediaMessage msg = obtainWXMediaMessage(mediaBuilder, textObj);
        sendMessageToWX(msg, mediaBuilder);
    }

    public void shareImage(MediaBuilder mediaBuilder) {
        WXImageObject imgObj = new WXImageObject(mediaBuilder.getImageBitmap());
        WXMediaMessage msg = obtainWXMediaMessage(mediaBuilder, imgObj);
        sendMessageToWX(msg, mediaBuilder);
    }

    public void shareWebPage(MediaBuilder mediaBuilder) {
        WXWebpageObject webPageObject = new WXWebpageObject();
        webPageObject.webpageUrl = mediaBuilder.getUrl();
        WXMediaMessage msg = obtainWXMediaMessage(mediaBuilder, webPageObject);
        sendMessageToWX(msg, mediaBuilder);
    }

    // title description url thumbnail
    public void shareMusic(MediaBuilder mediaBuilder) {
        WXMusicObject musicObject = new WXMusicObject();
        musicObject.musicUrl = mediaBuilder.getUrl();
        WXMediaMessage msg = obtainWXMediaMessage(mediaBuilder, musicObject);
        sendMessageToWX(msg, mediaBuilder);
    }

    // title description url thumbnail
    public void shareVideo(MediaBuilder mediaBuilder) {
        WXVideoObject videoObject = new WXVideoObject();
        videoObject.videoUrl = mediaBuilder.getUrl();
        WXMediaMessage msg = obtainWXMediaMessage(mediaBuilder, videoObject);
        sendMessageToWX(msg, mediaBuilder);
    }

    public boolean sendMessageToWX(WXMediaMessage msg, MediaBuilder mediaBuilder) {
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = mediaBuilder.getTransaction() == null ? String.valueOf(System.currentTimeMillis()) : null;
        req.message = msg;
        req.scene = mediaBuilder.getScene();
        return getWeChatApi().sendReq(req);
    }

    private WXMediaMessage obtainWXMediaMessage(MediaBuilder builder, WXMediaMessage.IMediaObject mediaObject) {
        WXMediaMessage msg = new WXMediaMessage(mediaObject);
        msg.title = builder.getTitle();
        msg.description = builder.getDescription();
        Bitmap thumbBitmap = builder.buildThumbnailBitmap();
        if (thumbBitmap != null) {
            msg.thumbData = BitmapUtils.bitmapToBytes(thumbBitmap);
        }
        return msg;
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

    public static class MediaBuilder {
        private static final int DEFAULT_THUMBNAIL_SIZE = 180;
        private String title;
        private String content;
        private String description;
        private String url;
        private Bitmap imageBitmap;
        private int thumbWidth = DEFAULT_THUMBNAIL_SIZE;
        private int thumbHeight = DEFAULT_THUMBNAIL_SIZE;
        private int scene = SendMessageToWX.Req.WXSceneTimeline;
        private String transaction;

        public MediaBuilder() {
        }

        public MediaBuilder setTitle(String text) {
            this.title = text;
            return this;
        }

        public MediaBuilder setContent(String text) {
            this.content = text;
            return this;
        }

        public MediaBuilder setDescription(String text) {
            this.description = text;
            return this;
        }

        public MediaBuilder setImageBitmap(Bitmap bitmap) {
            this.imageBitmap = bitmap;
            return this;
        }

        public MediaBuilder setThumbnailSize(int width, int height) {
            this.thumbWidth = width;
            this.thumbHeight = height;
            return this;
        }

        public MediaBuilder setScene(int scene) {
            this.scene = scene;
            return this;
        }

        public MediaBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

        public String getDescription() {
            return description;
        }

        public String getUrl() {
            return url;
        }

        public Bitmap getImageBitmap() {
            return imageBitmap;
        }

        public int getThumbWidth() {
            return thumbWidth;
        }

        public int getThumbHeight() {
            return thumbHeight;
        }

        public int getScene() {
            return scene;
        }

        public String getTransaction() {
            return transaction;
        }

        public Bitmap buildThumbnailBitmap() {
            if (imageBitmap == null) {
                return null;
            }
            return Bitmap.createScaledBitmap(imageBitmap, thumbWidth, thumbHeight, true);
        }
    }
}
