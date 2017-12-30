package com.onyx.jdread.shop.cloud.api;

import com.onyx.jdread.common.AppBaseInfo;
import com.onyx.jdread.shop.cloud.entity.jdbean.AddOrDelFromCartBean;
import com.onyx.jdread.shop.common.CloudApiContext;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
/**
 * Created by 12 on 2017/4/24.
 */

public interface AddOrDeleteFromCartService {
    @POST("client.action")
    Call<AddOrDelFromCartBean> addOrDeleteFromCart(@Query(CloudApiContext.NewBookDetail.FUNCTION_ID) String functionID,
                                                   @Query(AppBaseInfo.BODY_KEY) String body,
                                                   @QueryMap Map<String, String> map);
}