package com.onyx.jdread.setting.request;

import android.graphics.Bitmap;

import com.onyx.android.sdk.data.rxrequest.data.cloud.base.RxBaseCloudRequest;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.library.utils.QRCodeUtil;
import com.onyx.jdread.setting.model.PswFindData;
import com.onyx.jdread.setting.utils.Constants;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.util.EncryptUtils;

import java.security.MessageDigest;
import java.util.Date;

import retrofit2.Call;

/**
 * Created by suicheng on 2018/2/8.
 */
public class RxLockPswFindQrCodeRequest extends RxBaseCloudRequest {

    private double expiredInterval = 1.5 * 60 * 1000; //default
    private Bitmap qrImage;
    private int width, height;
    private String baseUrl;
    private String generateSidString;

    public RxLockPswFindQrCodeRequest(String baseUrl, int width, int height) {
        this.baseUrl = baseUrl;
        this.width = width;
        this.height = height;
    }

    public Bitmap getQrImage() {
        return qrImage;
    }

    public void setExpiredInterval(double expiredInterval) {
        this.expiredInterval = expiredInterval;
    }

    public void setGenerateSidString(String str) {
        this.generateSidString = str;
    }

    @Override
    public RxLockPswFindQrCodeRequest call() throws Exception {
        String originData = getGenerateSidString();
        byte[] encryptedData = EncryptUtils.encryptAES2Base64(
                originData.getBytes(),
                getKeyMD5Array(Constants.AES_KEY_FIND_PSW),
                Constants.AES_ECB_PKCS5PADDING_TRANSFORMATION,
                null
        );
        Call call = CloudApiContext.getOnyxService(baseUrl)
                .generatePasswordFindUrl(new String(encryptedData));
        String url = call.request().url().toString();
        qrImage = QRCodeUtil.createQRImage(url, width, height);
        return this;
    }

    private byte[] getKeyMD5Array(String key) throws Exception {
        return MessageDigest.getInstance("MD5").digest(key.getBytes());
    }

    private String getGenerateSidString() {
        if (StringUtils.isNotBlank(generateSidString)) {
            return generateSidString;
        }
        PswFindData pswFindData = new PswFindData();
        pswFindData.mac = NetworkUtil.getMacAddress(getAppContext());
        pswFindData.expiredTime = new Date((long) (System.currentTimeMillis() + expiredInterval));
        return JSONObjectParseUtils.toJson(pswFindData);
    }
}
