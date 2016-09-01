package com.onyx.android.sdk.data.v1;

import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.Captcha;
import com.onyx.android.sdk.data.model.Device;
import com.onyx.android.sdk.data.model.OnyxAccount;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created by zhuzeng on 8/10/16.
 */
public interface OnyxAccountService {

    @POST("account/signup")
    Call<ResponseBody> signup(@Body final OnyxAccount account);

    @PUT("account/signin")
    Call<ResponseBody> signin(@Body final OnyxAccount account);

    @PUT("account/signout")
    Call<ResponseBody> signout(@Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    @POST("account/devices")
    Call<Device> addDevice(@Body final Device device, @Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    @GET("captcha")
    Call<Captcha> getCaptcha();
}
