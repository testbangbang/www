package com.onyx.jdread.personal.cloud.api;

import com.onyx.jdread.main.common.AppBaseInfo;
import com.onyx.jdread.personal.cloud.entity.jdbean.BoughtBookResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by li on 2018/1/8.
 */

public interface GetBoughtService {
    @POST("client.action")
    Call<BoughtBookResultBean> getBoughtBook(@Query(CloudApiContext.NewBookDetail.FUNCTION_ID) String functionID,
                                             @Query(AppBaseInfo.BODY_KEY) String body,
                                             @QueryMap Map<String, String> map);
}
