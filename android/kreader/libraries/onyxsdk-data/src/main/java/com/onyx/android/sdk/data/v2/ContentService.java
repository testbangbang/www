package com.onyx.android.sdk.data.v2;

import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.CreateInformalEssayBean;
import com.onyx.android.sdk.data.model.DeleteGroupMemberBean;
import com.onyx.android.sdk.data.model.GroupNameExistBean;
import com.onyx.android.sdk.data.model.InformalEssayBean;
import com.onyx.android.sdk.data.model.ProductCart;
import com.onyx.android.sdk.data.model.ProductOrder;
import com.onyx.android.sdk.data.model.ProductResult;
import com.onyx.android.sdk.data.model.ReadingRateBean;
import com.onyx.android.sdk.data.model.v2.AddCommentRequestBean;
import com.onyx.android.sdk.data.model.v2.AllGroupBean;
import com.onyx.android.sdk.data.model.v2.AuthToken;
import com.onyx.android.sdk.data.model.v2.BaseAuthAccount;
import com.onyx.android.sdk.data.model.v2.ChangePendingGroupBean;
import com.onyx.android.sdk.data.model.v2.CloudLibrary;
import com.onyx.android.sdk.data.model.v2.CloudMetadata;
import com.onyx.android.sdk.data.model.v2.CreateBookReportRequestBean;
import com.onyx.android.sdk.data.model.v2.CreateBookReportResult;
import com.onyx.android.sdk.data.model.v2.CreateGroupCommonBean;
import com.onyx.android.sdk.data.model.v2.GetBookReportList;
import com.onyx.android.sdk.data.model.v2.GetInformalEssayBean;
import com.onyx.android.sdk.data.model.v2.GetReadingRateBean;
import com.onyx.android.sdk.data.model.v2.GetSharedImpressionResult;
import com.onyx.android.sdk.data.model.v2.GetSharedInformalResult;
import com.onyx.android.sdk.data.model.v2.GroupBean;
import com.onyx.android.sdk.data.model.v2.GroupMemberBean;
import com.onyx.android.sdk.data.model.v2.IndexService;
import com.onyx.android.sdk.data.model.v2.JoinGroupBean;
import com.onyx.android.sdk.data.model.v2.NewPassword;
import com.onyx.android.sdk.data.model.v2.PayBean;
import com.onyx.android.sdk.data.model.v2.PendingGroupBean;
import com.onyx.android.sdk.data.model.v2.ProductRequestBean;
import com.onyx.android.sdk.data.model.v2.SearchGroupBean;
import com.onyx.android.sdk.data.model.v2.ShareBookReportRequestBean;
import com.onyx.android.sdk.data.model.v2.ShareBookReportResult;
import com.onyx.android.sdk.data.model.v2.SignUpBean;
import com.onyx.android.sdk.data.model.v2.UploadReadingRateBean;
import com.onyx.android.sdk.data.model.v2.VerifyCode;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by suicheng on 2017/4/26.
 */

public interface ContentService {
    String CONTENT_AUTH_PREFIX = "Bearer ";

    @GET("devices/findByMac")
    Call<IndexService> getIndexService(@Query("mac") final String macAddress,
                                       @Query("installationId") final String installationId,
                                       @Query("model") final String model);

    @POST("auth/local")
    Call<AuthToken> getAccountToken(@Body final BaseAuthAccount account);

    @GET("users/me")
    Call<ResponseBody> getAccount();

    @GET("librarys/my")
    Call<List<CloudLibrary>> loadLibraryList();

    @GET("librarys/books")
    Call<ProductResult<CloudMetadata>> loadBookList(@Query(Constant.WHERE_TAG) final String param);

    @GET("librarys/{id}/books")
    Call<ProductResult<CloudMetadata>> loadBookList(@Path(Constant.ID_TAG) final String libraryId,
                                                    @Query(Constant.WHERE_TAG) final String param);

    @GET("books/{id}")
    Call<CloudMetadata> loadBook(@Path(Constant.ID_TAG) final String idString);

    @POST("/api/users")
    Call<AuthToken> signUp(@Body final SignUpBean signUpBean);

    @GET("/api/groups")
    Call<List<GroupBean>> getGroups();

    @GET("/api/groups/me")
    Call<List<GroupBean>> getMyGroup();

    @GET("librarys/{id}/library")
    Call<QueryResult<CloudLibrary>> loadChildLibraryList(@Path(Constant.ID_TAG) final String libraryId);

    @POST("orders")
    Call<ProductOrder<CloudMetadata>> createOrders(@Body final ProductRequestBean product);

    @GET("orders/{id}/pay")
    Call<PayBean> pay(@Path(Constant.ID_TAG) final String id);

    @GET("orders/{id}")
    Call<ProductOrder<CloudMetadata>> getOrder(@Path(Constant.ID_TAG) final String id);

    @GET("/api/groups/{id}")
    Call<CreateGroupCommonBean> getYearData(@Path(Constant.ID_TAG) final String id);

    @POST("carts")
    Call<ProductCart<CloudMetadata>> addProduct(@Body final ProductRequestBean product);

    @POST("/api/groups")
    Call<CreateGroupCommonBean> createGroup(@Body final CreateGroupCommonBean product);

    @POST("/api/JoinGroups")
    Call<List<JoinGroupBean>> joinGroup(@Body final JoinGroupBean product);

    @GET("/api/groups/checkExists")
    Call<GroupNameExistBean> checkExist(@Query(Constant.TEXT_TAG) final String text, @Query(Constant.PARENT_TAG) final String parent);

    @GET("/api/groups/search")
    Call<List<CreateGroupCommonBean>> searchSchool(@Query(Constant.TEXT_TAG) final String text, @Query(Constant.PARENT_TAG) final String parent);

    @GET("/api/groups/searchWithCreator")
    Call<List<SearchGroupBean>> getRelatedGroup(@Query(Constant.TEXT_TAG) final String text);

    @GET("carts")
    Call<QueryResult<ProductCart<CloudMetadata>>> getCartProducts();

    @POST("carts/remove")
    Call<ProductOrder<CloudMetadata>> removeProduct(@Body final ProductRequestBean product);

    @GET("books/search")
    Call<QueryResult<CloudMetadata>> search(@Query(Constant.TEXT_TAG) final String text);

    @GET("/api/groups/me")
    Call<List<AllGroupBean>> getAllGroups();

    @GET("/api/JoinGroups/pending")
    Call<List<PendingGroupBean>> getPendingGroups();

    @GET("/api/groups/{id}/users")
    Call<GroupMemberBean> getGroupMember(@Path(Constant.ID_TAG) final String id, @Query(Constant.WHERE_TAG) final String param);

    @GET("/api/groups/{id}/searchUsers")
    Call<GroupMemberBean> searchGroupMember(@Path(Constant.ID_TAG) final String id, @Query(Constant.TEXT_TAG) final String offset);

    @GET("/api/JoinGroups/{id}/change")
    Call<ChangePendingGroupBean> changePendingGroupState(@Path(Constant.ID_TAG) final String id, @Query(Constant.STATUS_TAG) final int param);

    @POST("/api/groups/{id}/removeusers")
    Call<DeleteGroupMemberBean> deleteGroupMember(@Path(Constant.ID_TAG) final String id, @Body final DeleteGroupMemberBean bean);

    @DELETE("/api/users/{id}/leaveGroups")
    Call<DeleteGroupMemberBean> deleteGroup(@Path(Constant.ID_TAG) final String id, @Query(Constant.GROUPS_TAG) final String param);

    @POST("/api/impressions")
    Call<CreateBookReportResult> createImpression(@Body final CreateBookReportRequestBean bean);

    @GET("/api/impressions/my")
    Call<GetBookReportList> getImpressionsList(@Query(Constant.GET_IMPRESSIONS_LIST_OFFSET) String offset,
                                               @Query(Constant.GET_IMPRESSIONS_LIST_LIMIT) String limit,
                                               @Query(Constant.GET_IMPRESSIONS_LIST_SORT_BY) String sortBy,
                                               @Query(Constant.GET_IMPRESSIONS_LIST_ORDER) String order);

    @GET("/api/librarys/{id}/impressions")
    Call<GetSharedImpressionResult> getSharedImpression(@Path(Constant.ID_TAG) final String id,
                                                        @Query(Constant.GET_IMPRESSIONS_LIST_OFFSET) String offset,
                                                        @Query(Constant.GET_IMPRESSIONS_LIST_LIMIT) String limit,
                                                        @Query(Constant.GET_IMPRESSIONS_LIST_SORT_BY) String sortBy,
                                                        @Query(Constant.GET_IMPRESSIONS_LIST_ORDER) String order);

    @GET("/api/librarys/{id}/informalEssay")
    Call<GetSharedInformalResult> getSharedInformalEssay(@Path(Constant.ID_TAG) final String id,
                                                         @Query(Constant.GET_IMPRESSIONS_LIST_OFFSET) String offset,
                                                         @Query(Constant.GET_IMPRESSIONS_LIST_LIMIT) String limit,
                                                         @Query(Constant.GET_IMPRESSIONS_LIST_SORT_BY) String sortBy,
                                                         @Query(Constant.GET_IMPRESSIONS_LIST_ORDER) String order);

    @GET("/api/ReadRecords/getByUser")
    Call<GetReadingRateBean> getSharedReadRecords(@Query(Constant.ID_TAG) final String id);

    @GET("/api/impressions/{id}")
    Call<CreateBookReportResult> getImpression(@Path(Constant.ID_TAG) final String id);

    @DELETE("/api/impressions/{id}")
    Call<String> deleteImpression(@Path(Constant.ID_TAG) final String id);

    @DELETE("/api/InformalEssays/{id}")
    Call<String> deleteInformalEssay(@Path(Constant.ID_TAG) final String id);

    @POST("/api/impressions/{id}/addComment")
    Call<CreateBookReportResult> addComment(@Path(Constant.ID_TAG) String id, @Body AddCommentRequestBean bean);

    @POST("/api/InformalEssays/{id}/addComment")
    Call<CreateInformalEssayBean> addInformalComment(@Path(Constant.ID_TAG) String id, @Body AddCommentRequestBean bean);

    @DELETE("/api/impressions/{id}/removeComment")
    Call<CreateBookReportResult> removeComment(@Path(Constant.ID_TAG) String id, @Query(Constant.REMOVE_COMMENT_ID) String commentId);

    @GET("users/phoneVerify")
    Call<VerifyCode> phoneVerify(@Query(Constant.PHONE_TAG) final String phone);

    @PUT("users/password/{token}")
    Call<VerifyCode> setPassword(@Path(Constant.TOKEN_TAG) String token, @Body final NewPassword newPassword);

    @POST("/api/librarys/{id}/impressions")
    Call<List<ShareBookReportResult>> shareImpression(@Path(Constant.ID_TAG) String id, @Body ShareBookReportRequestBean bean);

    @POST("/api/librarys/{id}/informalEssay")
    Call<List<ShareBookReportResult>> shareInformalEssay(@Path(Constant.ID_TAG) String id, @Body ShareBookReportRequestBean bean);

    @POST("/api/librarys/{id}/readRecords")
    Call<List<ShareBookReportResult>> shareReadingRate(@Path(Constant.ID_TAG) String id, @Body ShareBookReportRequestBean bean);

    @POST("/api/InformalEssays")
    Call<CreateInformalEssayBean> uploadInformalEssay(@Body InformalEssayBean param);

    @GET("/api/InformalEssays")
    Call<GetInformalEssayBean> getInformalEssay(@Query(Constant.WHERE_TAG) final String param);

    @POST("/api/ReadRecords")
    Call<UploadReadingRateBean> uploadReadingRate(@Body ReadingRateBean param);

    @GET("/api/librarys/{id}/readRecords")
    Call<GetReadingRateBean> getReadingRate(@Path(Constant.ID_TAG) final String id, @Query(Constant.WHERE_TAG) final String param);
}
