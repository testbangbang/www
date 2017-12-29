package com.onyx.jdread.personal.cloud.api;

import com.onyx.jdread.common.AppBaseInfo;
import com.onyx.jdread.personal.cloud.entity.jdbean.UserInfoBean;
import com.onyx.jdread.personal.common.CloudApiContext;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by 12 on 2017/3/31.
 */

public interface GetUserInfoService {
    @POST("client.action")
    Call<UserInfoBean> getUserInfo(@Query(CloudApiContext.NewBookDetail.FUNCTION_ID) String functionID,
                                   @Query(AppBaseInfo.BODY_KEY) String body,
                                   @QueryMap Map<String, String> map);
}