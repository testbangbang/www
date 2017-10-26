package com.onyx.android.sun.cloud.service;

import com.onyx.android.sun.cloud.bean.HomeworkFinishedResultBean;
import com.onyx.android.sun.cloud.bean.HomeworkUnfinishedResultBean;
import com.onyx.android.sun.cloud.bean.PersonalAbilityResultBean;
import com.onyx.android.sun.cloud.bean.SubmitPracticeResultBean;
import com.onyx.android.sun.cloud.bean.TaskBean;
import com.onyx.android.sun.cloud.bean.UserLoginResultBean;
import com.onyx.android.sun.cloud.bean.UserLogoutResultBean;
import com.onyx.android.sun.common.CloudApiContext;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by li on 2017/10/10.
 */

public interface ContentService {
    @GET("api/practice")
    Call<HomeworkUnfinishedResultBean> getHomeworkUnfinished(@Query(CloudApiContext.Practices.STATUS)String status,
                                                             @Query(CloudApiContext.Practices.STUDENTID)String studentId,
                                                             @Query(CloudApiContext.Practices.PAGE)String page,
                                                             @Query(CloudApiContext.Practices.SIZE)String size,
                                                             @Query(CloudApiContext.Practices.COURSE)String course,
                                                             @Query(CloudApiContext.Practices.TYPE)String type,
                                                             @Query(CloudApiContext.Practices.STARTTIME)String starttime,
                                                             @Query(CloudApiContext.Practices.ENDTIME)String endtime);

    @GET("api/practice")
    Call<HomeworkFinishedResultBean> getHomeworkFinished(@Query(CloudApiContext.Practices.STATUS)String status,
                                                         @Query(CloudApiContext.Practices.STUDENTID)String studentId,
                                                         @Query(CloudApiContext.Practices.PAGE)String page,
                                                         @Query(CloudApiContext.Practices.SIZE)String size,
                                                         @Query(CloudApiContext.Practices.COURSE)String course,
                                                         @Query(CloudApiContext.Practices.TYPE)String type,
                                                         @Query(CloudApiContext.Practices.STARTTIME)String starttime,
                                                         @Query(CloudApiContext.Practices.ENDTIME)String endtime);

    @GET("/api/practice/{id}")
    Call<TaskBean> getTaskDetail(@Path(CloudApiContext.Practices.ID) int id);

    @GET("api/message/all")
    Call<HomeworkUnfinishedResultBean> getMessage(@Query(CloudApiContext.Message.STUDENTID) String studentId,
                                         @Query(CloudApiContext.Message.PAGE) String page,
                                         @Query(CloudApiContext.Message.SIZE) String size);

    @GET("api/advanced/ability/own")
    Call<PersonalAbilityResultBean> getSubjectAbility(@Query(CloudApiContext.SubjectAbility.ID) String id);

    @FormUrlEncoded
    @POST("api/user/login")
    Call<UserLoginResultBean> userLogin(@Field(CloudApiContext.UserInfo.ACCOUNT) String account,
                                        @Field(CloudApiContext.UserInfo.PASSWORD) String password);

    @POST("api/practice/{id}")
    Call<SubmitPracticeResultBean> submitPractice(@Path(CloudApiContext.Practices.ID) int id,
                                                  @Query(CloudApiContext.Practices.STUDENTID)int studentId,
                                                  @Body RequestBody practiceBeanBody);

    @FormUrlEncoded
    @POST("api/user/logout")
    Call<UserLogoutResultBean> userLogout(@Field(CloudApiContext.UserInfo.ACCOUNT) String account);
}
