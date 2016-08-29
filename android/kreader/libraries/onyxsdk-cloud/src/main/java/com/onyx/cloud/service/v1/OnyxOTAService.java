package com.onyx.cloud.service.v1;

import com.onyx.cloud.Constant;
import com.onyx.cloud.model.Firmware;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by suicheng on 2016/8/12.
 */
public interface OnyxOTAService {

    @GET("firmware/update")
    Call<Firmware> firmwareUpdate(@Query(Constant.WHERE_TAG) final String param);
}
