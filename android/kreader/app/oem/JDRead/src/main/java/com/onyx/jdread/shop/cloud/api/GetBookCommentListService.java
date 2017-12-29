package com.onyx.jdread.shop.cloud.api;

import com.onyx.jdread.common.AppBaseInfo;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookCommentsResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by 12 on 2017/4/5.
 */

public interface GetBookCommentListService {
    @POST("client.action")
    Call<BookCommentsResultBean> getBookCommentsList(@Query(CloudApiContext.NewBookDetail.FUNCTION_ID) String functionID,
                                                     @Query(AppBaseInfo.BODY_KEY) String body,
                                                     @QueryMap Map<String, String> map);
}
