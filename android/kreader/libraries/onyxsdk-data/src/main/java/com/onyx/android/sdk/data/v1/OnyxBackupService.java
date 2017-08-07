package com.onyx.android.sdk.data.v1;

import com.onyx.android.sdk.data.model.CloudBackupData;
import com.onyx.android.sdk.data.model.JsonRespone;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by ming on 2017/7/22.
 */

public interface OnyxBackupService {

    @Multipart
    @POST("AppDatas/uploadData")
    Call<JsonRespone> uploadBackupFile(@Part MultipartBody.Part file,
                                       @Part final MultipartBody.Part name,
                                       @Part final MultipartBody.Part packageName,
                                       @Part final MultipartBody.Part version,
                                       @Part final MultipartBody.Part md5,
                                       @Part final MultipartBody.Part mac);

    @GET("AppDatas/findBackupDatas")
    Call<CloudBackupData> getBackupDatas(@Query("package") final String packageName, @Query("deviceMAC") final String mac);

}
