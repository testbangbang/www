package com.onyx.cloud.service.v1;

import com.onyx.cloud.Constant;
import com.onyx.cloud.model.Captcha;
import com.onyx.cloud.model.Device;
import com.onyx.cloud.model.OnyxAccount;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created by zhuzeng on 8/10/16.
 */
public interface OnyxAccountService {

    @POST("account/signup")
    Call<ResponseBody> signup(@Body final OnyxAccount account);

    @POST("account/signin")
    Call<ResponseBody> signin(@Body final OnyxAccount account);

    @POST("account/devices")
    Call<Device> addDevice(@Body final Device device, @Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    @GET("captcha")
    Call<Captcha> getCaptcha();
}
