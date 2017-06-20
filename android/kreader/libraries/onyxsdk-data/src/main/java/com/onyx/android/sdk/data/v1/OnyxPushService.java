package com.onyx.android.sdk.data.v1;

import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.Product;
import com.onyx.android.sdk.data.model.ProductResult;
import com.onyx.android.sdk.data.model.PushOssProduct;
import com.onyx.android.sdk.data.model.PushRecord;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by suicheng on 2016/10/14.
 */

public interface OnyxPushService {

    @GET("push/message")
    Call<ProductResult<PushRecord>> pushRecordList(@Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    @GET("push/broadcast")
    Call<ProductResult<PushRecord>> pushBroadcastList(@Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    @POST("push")
    Call<PushRecord> pushMessage(@Body final JSONObject jsonObject, @Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

    @POST("push/saveAndPush")
    Call<Product> pushSavingBook(@Body final PushOssProduct pushOssProduct, @Header(Constant.SESSION_TOKEN_TAG) final String sessionToken);

}
