package com.onyx.android.sun.cloud.service;

import com.onyx.android.sun.cloud.bean.PracticesResultBean;
import com.onyx.android.sun.common.CloudApiContext;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by li on 2017/10/10.
 */

public interface ContentService {
    @GET("api/practice")
    Call<PracticesResultBean> getPractice(@Query(CloudApiContext.Practices.STATUS)String status,
                                          @Query(CloudApiContext.Practices.STUDENTID)String studentId,
                                          @Query(CloudApiContext.Practices.PAGE)String page,
                                          @Query(CloudApiContext.Practices.SIZE)String size,
                                          @Query(CloudApiContext.Practices.COURSE)String course,
                                          @Query(CloudApiContext.Practices.TYPE)String type,
                                          @Query(CloudApiContext.Practices.STARTTIME)String starttime,
                                          @Query(CloudApiContext.Practices.ENDTIME)String endtime);
}
