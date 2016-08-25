package com.onyx.cloud.service;

import com.onyx.cloud.Constant;
import com.onyx.cloud.model.DownloadLink;
import com.onyx.cloud.model.Product;
import com.onyx.cloud.model.ProductContainer;
import com.onyx.cloud.model.ProductResult;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
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

    @GET("book/{id}/links")
    Call<List<DownloadLink>> bookDownloadLink(@Path("id") final String uniqueId);

    @GET("book/list/recommeded")
    Call<ProductResult<Product>> bookRecommendedList();

    @GET("book/list/recent")
    Call<ProductResult<Product>> bookRecentList();

    @GET("book/search")
    Call<ProductResult<Product>> bookSearch(@Query(Constant.WHERE_TAG) final String param);

    @GET("category")
    Call<ProductResult<ProductContainer>> bookContainer(@Query(Constant.WHERE_TAG) final String param);
}
