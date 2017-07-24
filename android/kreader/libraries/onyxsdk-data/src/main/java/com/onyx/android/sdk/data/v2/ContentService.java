package com.onyx.android.sdk.data.v2;

import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.v2.AdminApplyModel;
import com.onyx.android.sdk.data.model.v2.CloudGroup;
import com.onyx.android.sdk.data.model.v2.Course;
import com.onyx.android.sdk.data.model.v2.DeviceBind;
import com.onyx.android.sdk.data.model.v2.GroupUserInfo;
import com.onyx.android.sdk.data.model.v2.IndexService;
import com.onyx.android.sdk.data.model.v2.AuthToken;
import com.onyx.android.sdk.data.model.v2.CloudLibrary;
import com.onyx.android.sdk.data.model.v2.CloudMetadata;
import com.onyx.android.sdk.data.model.v2.BaseAuthAccount;
import com.onyx.android.sdk.data.model.ProductResult;
import com.onyx.android.sdk.data.model.v2.NeoAccountBase;
import com.onyx.android.sdk.data.utils.ResultCode;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
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
    Call<IndexService> getIndexService(@Query(Constant.MAC_TAG) final String macAddress,
                                       @Query(Constant.INSTALLATIONId_TAG) final String installationId);

    @POST("auth/local")
    Call<AuthToken> getAccountToken(@Body final BaseAuthAccount account);

    @GET("users/me")
    Call<ResponseBody> getAccount();

    @GET("courses/my")
    Call<List<Course>> getMyCourses();

    @GET("librarys/my")
    Call<List<CloudLibrary>> loadLibraryList();

    @GET("librarys/books")
    Call<ProductResult<CloudMetadata>> loadBookList(@Query(Constant.WHERE_TAG) final String param);

    @GET("librarys/{id}/library")
    Call<QueryResult<CloudLibrary>> loadLibraryList(@Path(Constant.ID_TAG) final String idString);

    @GET("librarys/{id}/books")
    Call<ProductResult<CloudMetadata>> loadBookList(@Path(Constant.ID_TAG) final String libraryId,
                                                    @Query(Constant.WHERE_TAG) final String param);

    @GET("books/{id}")
    Call<CloudMetadata> loadBook(@Path(Constant.ID_TAG) final String idString);

    @POST("groups/{id}/createUserByDevices")
    Call<NeoAccountBase> createUserByDevice(@Path(Constant.ID_TAG) final String groupId,
                                            @Body final DeviceBind deviceBind);

    @GET("groups/{id}/groupusers")
    Call<List<NeoAccountBase>> getGroupUserList(@Path(Constant.ID_TAG) final String groupId);

    @GET("groups/{id}")
    Call<CloudGroup> getGroup(@Path(Constant.ID_TAG) final String parentGroupId);

    @GET("groups/recursive")
    Call<List<CloudGroup>> getRecursiveGroupList();

    // get groupList user belongTo
    @GET("groups/me")
    Call<List<CloudGroup>> getMyGroupList();

    @POST("adusers")
    Call<ResponseBody> applyAdminRequest(@Body final AdminApplyModel applyModel);

    @GET("adusers/findByMac")
    Call<IndexService> getAdminIndexService(@Query(Constant.MAC_TAG) final String macAddress);

    @GET("users/findByDeviceMac")
    Call<GroupUserInfo> getGroupUserInfo(@Query(Constant.MAC_TAG) final String macAddress);

    @PUT("users/{id}/bindDevice")
    Call<ResponseBody> bindUserByDevice(@Path(Constant.ID_TAG) final String userId,
                                            @Body final DeviceBind deviceBind);

    @PUT("users/{id}/unbindDevice")
    Call<ResponseBody> unbindUserByDevice(@Path(Constant.ID_TAG) final String userId,
                                            @Body final DeviceBind deviceBind);

    @GET("adapplys/phoneVerify")
    Call<ResponseBody> applyPhoneVerify(@Query(Constant.PHONE_TAG) final String phone);
}
