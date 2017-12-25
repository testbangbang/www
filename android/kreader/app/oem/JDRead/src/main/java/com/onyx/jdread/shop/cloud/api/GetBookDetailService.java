package com.onyx.jdread.shop.cloud.api;


import com.onyx.jdread.common.AppBaseInfo;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by huxiaomao on 2016/12/19.
 */

public interface GetBookDetailService {
    @POST("client.action")
    Call<BookDetailResultBean> getBookDetail(@Query(CloudApiContext.NewBookDetail.FUNCTION_ID) String functionID,
                                             @Query(AppBaseInfo.BODY_KEY) String body);
}