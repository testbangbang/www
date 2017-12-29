package com.onyx.jdread.personal.cloud.api;

import com.onyx.jdread.personal.cloud.entity.jdbean.SyncLoginInfoBean;
import com.onyx.jdread.personal.common.CloudApiContext;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by 12 on 2017/3/31.
 */

public interface GetSyncLoginInfoService {
    @POST("client.action")
    Call<SyncLoginInfoBean> getSyncLoginInfo(@Query(CloudApiContext.NewBookDetail.FUNCTION_ID) String functionID,
                                             @QueryMap Map<String, String> map);
}
