package com.onyx.jdread.setting.service;

import com.onyx.jdread.setting.model.PswSettingData;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by wang.suiccheng on 2018/2/9.
 */
public interface OnyxService {

    @POST("devices")
    Call<ResponseBody> passwordSetting(@Body final PswSettingData data);
}
