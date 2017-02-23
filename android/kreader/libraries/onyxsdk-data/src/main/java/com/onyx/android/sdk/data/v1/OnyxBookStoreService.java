package com.onyx.android.sdk.data.v1;

import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.Category;
import com.onyx.android.sdk.data.model.Comment;
import com.onyx.android.sdk.data.model.DownloadLink;
import com.onyx.android.sdk.data.model.Product;
import com.onyx.android.sdk.data.model.ProductResult;
import com.onyx.android.sdk.data.model.ProductShared;

import java.util.List;

import okhttp3.ResponseBody;
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
 * Created by zhuzeng on 8/10/16.
 */
public interface OnyxBookStoreService {

    @GET("book/list")
    Call<ProductResult<Product>> bookList(@Query(Constant.WHERE_TAG) final String param);

    @GET("book/{id}")
    Call<Product> book(@Path("id") final String uniqueId);

    //文件格式,取值:[pdf,epub,txt]
    @GET("book/{id}/data")
    Call<ResponseBody> getBookBytes(@Path("id") final String uniqueId,
                                    @Query(Constant.FORMAT_TAG) final String format,
                                    @Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    //注：where条件中的key字段填文件格式，如：pdf，value字段填md5,两者为必填项
    @GET("book/{id}")
    Call<Product> getBookByMd5(@Path("id") final String uniqueId,
                               @Query(Constant.WHERE_TAG) final String param,
                               @Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    @GET("book/{id}/links")
    Call<List<DownloadLink>> bookDownloadLink(@Path("id") final String uniqueId);

    @GET("book/list/recommended")
    Call<ProductResult<Product>> bookRecommendedList(@Query(Constant.WHERE_TAG) final String param);

    @GET("book/list/recent")
    Call<ProductResult<Product>> bookRecentList();

    @GET("book/search")
    Call<ProductResult<Product>> bookSearch(@Query(Constant.WHERE_TAG) final String param);

    @GET("category")
    Call<List<Category>> bookContainerList();

    @GET("category/{id}")
    Call<Category> bookContainer(@Path("id") final String idString);

    @DELETE("book/{id}/data")
    Call<ResponseBody> deleteBook(@Path("id") final String uniqueId,
                                  @Query(Constant.FORMAT_TAG) final String format,
                                  @Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    @GET("book/{id}/{type}/cover")
    Call<ResponseBody> getBookCover(@Path("id") final String uniqueId,
                                    @Path(Constant.TYPE_TAG) final String type);

    @GET("book/{id}/comment/list")
    Call<ProductResult<Comment>> getBookCommentList(@Path("id") final String uniqueId,
                                                    @Query(Constant.WHERE_TAG) final String param);

    @GET("book/{pid}/comment/{cid}")
    Call<Comment> getBookComment(@Path("pid") final String bookId,
                                 @Path("cid") final String commentId);

    @POST("book/{id}/comment")
    Call<Comment> postBookComment(@Path("id") final String bookId,
                                  @Body Comment comment,
                                  @Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    @PUT("book/{pid}/comment/{cid}")
    Call<Comment> updateBookComment(@Path("pid") final String bookId,
                                    @Path("cid") final String commentId,
                                    @Body Comment comment,
                                    @Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    @DELETE("book/{pid}/comment/{cid}")
    Call<ResponseBody> deleteBookComment(@Path("pid") final String bookId,
                                         @Path("cid") final String commentId,
                                         @Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    @PUT("book/{pid}/comment/{cid}/up")
    Call<Comment> supportBookComment(@Path("pid") final String bookId,
                                     @Path("cid") final String commentId,
                                     @Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    @PUT("book/{pid}/comment/{cid}/down")
    Call<Comment> disagreeBookComment(@Path("pid") final String bookId,
                                      @Path("cid") final String commentId,
                                      @Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    @GET("book/share/list")
    Call<ProductResult<ProductShared>> productSharedList(@Query(Constant.WHERE_TAG) final String queryParam);
}
