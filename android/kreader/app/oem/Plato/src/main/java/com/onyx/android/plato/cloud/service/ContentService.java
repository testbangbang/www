package com.onyx.android.plato.cloud.service;

import com.onyx.android.plato.cloud.bean.ChangePasswordResultBean;
import com.onyx.android.plato.cloud.bean.GetAnalysisBean;
import com.onyx.android.plato.cloud.bean.GetCorrectedTaskResultBean;
import com.onyx.android.plato.cloud.bean.GetReportListBean;
import com.onyx.android.plato.cloud.bean.GetStudyReportDetailResultBean;
import com.onyx.android.plato.cloud.bean.GetSubjectAbilityResultBean;
import com.onyx.android.plato.cloud.bean.GetSubjectBean;
import com.onyx.android.plato.cloud.bean.HomeworkFinishedResultBean;
import com.onyx.android.plato.cloud.bean.HomeworkUnfinishedResultBean;
import com.onyx.android.plato.cloud.bean.InsertParseRequestBean;
import com.onyx.android.plato.cloud.bean.LoginRequestBean;
import com.onyx.android.plato.cloud.bean.ModifyPasswordBean;
import com.onyx.android.plato.cloud.bean.PersonalAbilityResultBean;
import com.onyx.android.plato.cloud.bean.PracticeParseResultBean;
import com.onyx.android.plato.cloud.bean.SubmitPracticeResultBean;
import com.onyx.android.plato.cloud.bean.TaskBean;
import com.onyx.android.plato.cloud.bean.UploadBean;
import com.onyx.android.plato.cloud.bean.UserCenterBean;
import com.onyx.android.plato.cloud.bean.UserLoginResultBean;
import com.onyx.android.plato.cloud.bean.UserLogoutResultBean;
import com.onyx.android.plato.common.CloudApiContext;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by li on 2017/10/10.
 */

public interface ContentService {
    @GET("api/practice")
    Call<HomeworkUnfinishedResultBean> getHomeworkUnfinished(@Query(CloudApiContext.Practices.STATUS) String status,
                                                             @Query(CloudApiContext.Practices.PAGE) String page,
                                                             @Query(CloudApiContext.Practices.SIZE) String size,
                                                             @Query(CloudApiContext.Practices.COURSE) String course,
                                                             @Query(CloudApiContext.Practices.TYPE) String type,
                                                             @Query(CloudApiContext.Practices.STARTTIME) String starttime,
                                                             @Query(CloudApiContext.Practices.ENDTIME) String endtime);

    @GET("api/practice")
    Call<HomeworkFinishedResultBean> getHomeworkFinished(@Query(CloudApiContext.Practices.STATUS) String status,
                                                         @Query(CloudApiContext.Practices.PAGE) String page,
                                                         @Query(CloudApiContext.Practices.SIZE) String size,
                                                         @Query(CloudApiContext.Practices.COURSE) String course,
                                                         @Query(CloudApiContext.Practices.TYPE) String type,
                                                         @Query(CloudApiContext.Practices.STARTTIME) String starttime,
                                                         @Query(CloudApiContext.Practices.ENDTIME) String endtime);

    @GET("/api/practice/{id}")
    Call<TaskBean> getTaskDetail(@Path(CloudApiContext.Practices.ID) int id);

    @GET("api/message/all")
    Call<HomeworkUnfinishedResultBean> getMessage(@Query(CloudApiContext.Message.PAGE) String page,
                                                  @Query(CloudApiContext.Message.SIZE) String size);

    @GET("api/message/del")
    Call<SubmitPracticeResultBean> deleteMessage(@Query(CloudApiContext.Message.ID) String id);

    @GET("api/advanced/ability/own")
    Call<PersonalAbilityResultBean> getSubjectAbility();

    @POST("api/auth/login")
    Call<UserLoginResultBean> userLogin(@Body LoginRequestBean bean);

    @POST("api/practice/{id}")
    Call<SubmitPracticeResultBean> submitPractice(@Path(CloudApiContext.Practices.ID) int id,
                                                  @Body RequestBody practiceBeanBody);

    @POST("api/practice/favorite")
    Call<SubmitPracticeResultBean> favoriteOrDeletePractice(@Body RequestBody requestBody);

    @GET("api/practice/review/{id}")
    Call<GetCorrectedTaskResultBean> getCorrectedTask(@Path(CloudApiContext.Practices.ID) int id);

    @GET("api/practice/log/analysis")
    Call<PracticeParseResultBean> getPracticeParse(@Query(CloudApiContext.Practices.ID) int id,
                                                   @Query(CloudApiContext.Practices.PID) int pid);

    @POST("api/practice/insertAnalysis")
    Call<SubmitPracticeResultBean> insertAnalysis(@Query(CloudApiContext.Practices.ID) int id,
                                                  @Query(CloudApiContext.Practices.PID) int pid,
                                                  @Body InsertParseRequestBean requestBody);

    @GET("api/practice/getAnalysis")
    Call<GetAnalysisBean> getAnalysis(@Query(CloudApiContext.Practices.ID) int id,
                                      @Query(CloudApiContext.Practices.PID) int pid);

    @POST("api/practice/exerciseResult/{id}/introspect")
    Call<SubmitPracticeResultBean> practiceIntrospection(@Path(CloudApiContext.Practices.ID) int id,
                                                         @Body RequestBody requestBody);

    @GET("api/subject/getSubject")
    Call<GetSubjectBean> getSubject();

    @GET("api/subject/getExerciseType")
    Call<GetSubjectBean> getExerciseType();

    @FormUrlEncoded
    @POST("api/user/logout")
    Call<UserLogoutResultBean> userLogout(@Field(CloudApiContext.UserInfo.ACCOUNT) String account);

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
    Call<GetReportListBean> getReportList(@Path(CloudApiContext.Practices.COURSE_ID) int courseId);

    @Multipart
    @POST("api/upload/getFileInfo")
    Call<UploadBean> getUploadKey(@Part MultipartBody.Part file);

    @GET("api/user/info")
    Call<UserCenterBean> getUserInfo();

    @PUT("api/user/passwd")
    Call<SubmitPracticeResultBean> modifyPassword(@Body ModifyPasswordBean bean);
}
