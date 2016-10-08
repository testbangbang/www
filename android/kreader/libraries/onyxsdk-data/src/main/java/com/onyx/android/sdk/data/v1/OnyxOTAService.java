package com.onyx.android.sdk.data.v1;

import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.ApplicationUpdate;
import com.onyx.android.sdk.data.model.Firmware;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by suicheng on 2016/8/12.
 */
public interface OnyxOTAService {

    @GET("firmware/update")
    Call<Firmware> ReleaseFirmwareUpdate(@Query(Constant.WHERE_TAG) final String param);

    @GET("firmware/updateTest")
    Call<Firmware> testFirmwareUpdate(@Query(Constant.WHERE_TAG) final String param);

    @GET("apps")
    Call<List<ApplicationUpdate>> getUpdateAppInfoList(@Query(Constant.WHERE_TAG) final String param);

    @GET("app")
    Call<ApplicationUpdate> getUpdateAppInfo(@Query(Constant.WHERE_TAG) final String param);
}
