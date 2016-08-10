package com.onyx.cloud.service;

import android.view.SurfaceHolder;
import com.onyx.cloud.model.OnyxAccount;
import com.onyx.cloud.model.Product;
import com.onyx.cloud.model.ProductResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

/**
 * Created by zhuzeng on 8/10/16.
 */
public interface OnyxBookStoreService {

    @GET("book/list")
    Call<ProductResult<Product>> bookList();


    @GET("book/{id}")
    Call<Product> book(@Path("id") final String uniqueId);
}
