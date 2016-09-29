package com.onyx.android.sdk.data.v1;

import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.Captcha;
import com.onyx.android.sdk.data.model.Device;
import com.onyx.android.sdk.data.model.OnyxAccount;
import com.onyx.android.sdk.data.utils.ProgressRequestBody;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created by zhuzeng on 8/10/16.
 */
public interface OnyxAccountService {

    @POST("account/signup")
    Call<OnyxAccount> signup(@Body final OnyxAccount account);

    @PUT("account/signin")
    Call<OnyxAccount> signin(@Body final OnyxAccount account);

    @PUT("account/signout")
    Call<OnyxAccount> signout(@Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    @GET("account")
    Call<OnyxAccount> getAccountInfo(@Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    @PUT("account")
    Call<OnyxAccount> updateAccountInfo(@Body final OnyxAccount account, @Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    @POST("account/activation")
    Call applyActivateAccount(@Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    @GET("account/activation")
    Call postActivateAccount(@Query(Constant.TOKEN_TAG) final String token);

    @POST("account/password/forgot")
    Call forgotAccountPwd(@Body final OnyxAccount account);

    @PUT("account/password/reset")
    Call resetAccountPwd(@Body final Map<String, String> map);

    @POST("account/device")
    Call<Device> addDevice(@Body final Device device, @Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    @GET("account/device")
    Call<List<Device>> getBoundDeviceList(@Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    @GET("account/device/{deviceId}")
    Call<Device> getBoundDevice(@Path("deviceId") String deviceId, @Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    @DELETE("account/device/{deviceId}")
    Call<ResponseBody> removeBoundDevice(@Path("deviceId") String deviceId, @Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    @POST("captcha")
    Call<Captcha> generateCaptcha();

    @Multipart
    @POST("account/avatar")
    Call<OnyxAccount> uploadAvatar(@Part MultipartBody.Part avatar, @Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    //支持进度条回调显示，不过文件名只能写死
    /*@Multipart
    @POST("account/avatar")
    Call<OnyxAccount> uploadAvatar(@Part("upload\"; filename=\"avatar\"") ProgressRequestBody file, @Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);*/
}
