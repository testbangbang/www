package com.onyx.android.sdk.data.v2;

import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.GroupNameExistBean;
import com.onyx.android.sdk.data.model.ProductCart;
import com.onyx.android.sdk.data.model.ProductOrder;
import com.onyx.android.sdk.data.model.ProductResult;
import com.onyx.android.sdk.data.model.v2.AuthToken;
import com.onyx.android.sdk.data.model.v2.BaseAuthAccount;
import com.onyx.android.sdk.data.model.v2.CloudLibrary;
import com.onyx.android.sdk.data.model.v2.CloudMetadata;
import com.onyx.android.sdk.data.model.v2.CreateGroupCommonBean;
import com.onyx.android.sdk.data.model.v2.GroupBean;
import com.onyx.android.sdk.data.model.v2.IndexService;
import com.onyx.android.sdk.data.model.v2.JoinGroupBean;
import com.onyx.android.sdk.data.model.v2.PayBean;
import com.onyx.android.sdk.data.model.v2.ProductRequestBean;
import com.onyx.android.sdk.data.model.v2.SearchGroupBean;
import com.onyx.android.sdk.data.model.v2.SignUpBean;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by suicheng on 2017/4/26.
 */

public interface ContentService {
    String CONTENT_AUTH_PREFIX = "Bearer ";

    @GET("devices/findByMac")
    Call<IndexService> getIndexService(@Query("mac") final String macAddress,
                                       @Query("installationId") final String installationId);

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
}
