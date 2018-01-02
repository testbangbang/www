package com.onyx.jdread.shop.cloud.api;

import com.onyx.jdread.main.common.AppBaseInfo;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryV2BooksResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by hehai on 17-3-31.
 */

public interface GetCategoryV2BooksService {
    @POST("client.action")
    Call<CategoryV2BooksResultBean> getCategoryBookListV2(@QueryMap Map<String, String> map,
                                                          @Query(CloudApiContext.NewBookDetail.FUNCTION_ID) String functionID,
                                                          @Query(AppBaseInfo.BODY_KEY) String body);
}