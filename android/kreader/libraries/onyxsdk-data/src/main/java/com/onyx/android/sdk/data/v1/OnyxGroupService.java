package com.onyx.android.sdk.data.v1;

import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.Group;
import com.onyx.android.sdk.data.model.Member;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by suicheng on 2016/9/20.
 */
public interface OnyxGroupService {

    @POST("group")
    Call<Group> createGroup(@Body final Group group, @Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    @GET("group")
    Call<List<Group>> getGroupList(@Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    @GET("group/{id}")
    Call<Group> getGroup(@Path("id") final long groupId,
                         @Query(Constant.WHERE_TAG) final String param,
                         @Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    @PUT("group/{id}")
    Call<Group> updateGroup(@Path("id") final long groupId,
                            @Body final Group group,
                            @Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    @DELETE("group/{id}")
    Call deleteGroup(@Path("id") final long groupId,
                     @Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    @GET("group/{groupId}/member")
    Call<List<Member>> getMemberList(@Path("id") final long groupId,
                                     @Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    @POST("group/{groupId}/member")
    Call<Member> addGroupMember(@Path("id") final long groupId,
                                @Body final Member member,
                                @Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    @DELETE("group/{groupId}/member/{accountId}")
    Call deleteGroupMember(@Path("groupId") final long groupId,
                           @Path("accountId") final long accountId,
                           @Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    @PUT("group/{groupId}/member/{accountId}")
    Call<Member> updateGroupMember(@Path("groupId") final long groupId,
                                   @Path("accountId") final long accountId,
                                   @Body final Member member,
                                   @Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

}
