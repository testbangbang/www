package com.onyx.android.sun.cloud.service;

import com.onyx.android.sun.cloud.bean.PersonalAbilityResultBean;
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

    @GET("api/message/all")
    Call<PracticesResultBean> getMessage(@Query(CloudApiContext.Message.STUDENTID) String studentId,
                                         @Query(CloudApiContext.Message.PAGE) String page,
                                         @Query(CloudApiContext.Message.SIZE) String size);

    @GET("api/advanced/ability/own")
    Call<PersonalAbilityResultBean> getSubjectAbility(@Query(CloudApiContext.SubjectAbility.ID) String id);
}
