package com.onyx.android.sdk.data.v1;

import com.onyx.android.sdk.data.model.JsonRespone;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by ming on 2017/5/31.
 */

public interface OnyxSyncService {

    @Multipart
    @POST("api/books/{id}/filedata")
    Call<JsonRespone> pushReaderData(@Path("id") String id, @Part MultipartBody.Part file, @Part final MultipartBody.Part md5);

}
