package com.onyx.android.sdk.data.request.cloud;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.OnyxAccount;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by suicheng on 2016/9/20.
 */
public class AccountAvatarUploadRequest extends BaseCloudRequest {

    private String avatarUrl;
    private File avatarFile;
    private String sessionToken;

    public AccountAvatarUploadRequest(File file, String sessionToken) {
        this.avatarFile = file;
        this.sessionToken = sessionToken;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        MediaType mediaType = MediaType.parse("image/*");
        RequestBody requestFile = RequestBody.create(mediaType, avatarFile);
        MultipartBody.Part partBody = MultipartBody.Part.createFormData(Constant.AVATAR_TAG, avatarFile.getName(), requestFile);
        Call<OnyxAccount> call = ServiceFactory.getAccountService(parent.getCloudConf().getApiBase())
                .uploadAvatar(partBody, sessionToken);
        Response<OnyxAccount> response = call.execute();
        if (response.isSuccessful()) {
            avatarUrl = response.body().avatarUrl;
        }
    }
}
