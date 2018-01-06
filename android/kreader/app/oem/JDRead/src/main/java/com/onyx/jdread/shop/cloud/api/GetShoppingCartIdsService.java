package com.onyx.jdread.shop.cloud.api;

import com.onyx.jdread.main.common.AppBaseInfo;
import com.onyx.jdread.shop.cloud.entity.jdbean.ShoppingCartBookIdsBean;
import com.onyx.jdread.shop.common.CloudApiContext;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by li on 2018/1/5.
 */

public interface GetShoppingCartIdsService {
    @POST("client.action")
    Call<ShoppingCartBookIdsBean> getCartBookIds(@Query(CloudApiContext.NewBookDetail.FUNCTION_ID) String functionID,
                                                 @Query(AppBaseInfo.BODY_KEY) String body,
                                                 @QueryMap Map<String, String> map);
}
