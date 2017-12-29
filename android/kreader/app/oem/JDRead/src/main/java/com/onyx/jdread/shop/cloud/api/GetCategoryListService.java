package com.onyx.jdread.shop.cloud.api;


import com.onyx.jdread.common.AppBaseInfo;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by huxiaomao on 2016/12/19.
 */

public interface GetCategoryListService {
    @POST("client.action")
    Call<CategoryListResultBean> getCategoryList(@QueryMap Map<String, String> map,
                                                 @Query(CloudApiContext.NewBookDetail.FUNCTION_ID) String functionID,
                                                 @Query(AppBaseInfo.BODY_KEY) String body);
}