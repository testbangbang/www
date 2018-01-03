package com.onyx.jdread.shop.cloud.api;

import com.onyx.jdread.main.common.AppBaseInfo;
import com.onyx.jdread.shop.cloud.entity.jdbean.CertBean;
import com.onyx.jdread.shop.common.CloudApiContext;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by 12 on 2017/4/8.
 */

public interface GetBookCertService {
    @POST("client.action")
    Call<CertBean> getBookCert(@Query(CloudApiContext.NewBookDetail.FUNCTION_ID) String functionID,
                               @QueryMap Map<String, String> map,
                               @Query(AppBaseInfo.BODY_KEY) String body);
}
