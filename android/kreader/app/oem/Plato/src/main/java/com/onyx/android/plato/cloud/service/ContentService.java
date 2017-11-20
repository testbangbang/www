package com.onyx.android.plato.cloud.service;

import com.onyx.android.plato.cloud.bean.ChangePasswordResultBean;
import com.onyx.android.plato.cloud.bean.GetCorrectedTaskResultBean;
import com.onyx.android.plato.cloud.bean.GetReportListBean;
import com.onyx.android.plato.cloud.bean.GetStudyReportDetailResultBean;
import com.onyx.android.plato.cloud.bean.GetSubjectAbilityResultBean;
import com.onyx.android.plato.cloud.bean.GetSubjectBean;
import com.onyx.android.plato.cloud.bean.HomeworkFinishedResultBean;
import com.onyx.android.plato.cloud.bean.HomeworkUnfinishedResultBean;
import com.onyx.android.plato.cloud.bean.PersonalAbilityResultBean;
import com.onyx.android.plato.cloud.bean.PracticeParseResultBean;
import com.onyx.android.plato.cloud.bean.SubmitPracticeResultBean;
import com.onyx.android.plato.cloud.bean.TaskBean;
import com.onyx.android.plato.cloud.bean.UserLoginResultBean;
import com.onyx.android.plato.cloud.bean.UserLogoutResultBean;
import com.onyx.android.plato.common.CloudApiContext;

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

    @GET("api/message/del")
    Call<SubmitPracticeResultBean> deleteMessage(@Query(CloudApiContext.Message.ID) String id,
                                                 @Query(CloudApiContext.Message.STUDENTID) String studentId);

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

    @POST("api/practice/favorite")
    Call<SubmitPracticeResultBean> favoriteOrDeletePractice(@Query(CloudApiContext.Practices.ID) int id,
                                                            @Body RequestBody requestBody);

    @GET("api/practice/review/{id}")
    Call<GetCorrectedTaskResultBean> getCorrectedTask(@Path(CloudApiContext.Practices.ID) int id,
                                                      @Query(CloudApiContext.Practices.STUDENTID)int studentId);

    @GET("api/practice/log/analysis")
    Call<PracticeParseResultBean> getPracticeParse(@Query(CloudApiContext.Practices.ID) int id,
                                                   @Query(CloudApiContext.Practices.PID) int pid,
                                                   @Query(CloudApiContext.Practices.STUDENTID) int studentId);

    @POST("api/practice/exerciseResult/{id}/introspect")
    Call<SubmitPracticeResultBean> practiceIntrospection(@Path(CloudApiContext.Practices.ID) int id,
                                                         @Body RequestBody requestBody);

    @GET("api/subject/getSubject")
    Call<GetSubjectBean> getSubject(@Query(CloudApiContext.Practices.STUDENTID) int studentId);

    @GET("api/subject/getExerciseType")
    Call<GetSubjectBean> getExerciseType();

    @FormUrlEncoded
    @POST("api/user/logout")
    Call<UserLogoutResultBean> userLogout(@Field(CloudApiContext.UserInfo.ACCOUNT) String account);

    @FormUrlEncoded
    @POST("api/user/changePassword")
    Call<ChangePasswordResultBean> changePassword(@Field(CloudApiContext.ChangePassword.ACCOUNT) String account,
                                                  @Field(CloudApiContext.ChangePassword.OLD_PASSWORD) String oldPassword,
                                                  @Field(CloudApiContext.ChangePassword.NEW_PASSWORD) String newPassword);
    @GET("/api/practice/{id}/report")
    Call<GetStudyReportDetailResultBean> getStudyReportDetail(@Path(CloudApiContext.Practices.ID) int id);

    @GET("api/advanced/ability")
    Call<GetSubjectAbilityResultBean> getSubjectAbility(@Query(CloudApiContext.SubjectAbility.ID) String id,
                                                        @Query(CloudApiContext.SubjectAbility.COURSE) String course,
                                                        @Query(CloudApiContext.SubjectAbility.TERM) String term);

    @GET("api/advanced/module/{id}/ability")
    Call<GetSubjectAbilityResultBean> getSubjectAbilityModule(@Path(CloudApiContext.SubjectAbility.ID) String id,
                                                        @Query(CloudApiContext.SubjectAbility.TERM) String term);

    @GET("api/practice/getReportList/{courseId}")
    Call<GetReportListBean> getReportList(@Path(CloudApiContext.Practices.COURSE_ID) int courseId,
                                          @Query(CloudApiContext.Practices.STUDENTID) int studentId);
}
