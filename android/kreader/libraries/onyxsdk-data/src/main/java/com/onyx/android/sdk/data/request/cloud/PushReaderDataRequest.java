package com.onyx.android.sdk.data.request.cloud;

import android.content.Context;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.JsonRespone;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

/**
 * Created by ming on 2017/5/31.
 */

public class PushReaderDataRequest extends BaseCloudRequest {

    private String uploadDBPath;
    private Context context;
    private String url;
    private String fileFullMd5;
    private String fileId;

    public PushReaderDataRequest(String uploadDBPath, Context context, String url, String fileFullMd5, String fileId) {
        this.uploadDBPath = uploadDBPath;
        this.context = context;
        this.url = url;
        this.fileFullMd5 = fileFullMd5;
        this.fileId = fileId;
    }

    @Override
    public void execute(final CloudManager parent) throws Exception {
        if (StringUtils.isNullOrEmpty(url)) {
            return;
        }
        if (!FileUtils.fileExist(uploadDBPath)) {
            return;
        }
        if (!NetworkUtil.isWiFiConnected(context)) {
            return;
        }
        if (StringUtils.isNullOrEmpty(fileFullMd5)) {
            return;
        }
        if (StringUtils.isNullOrEmpty(fileId)) {
            return;
        }
        File dbFile = new File(uploadDBPath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("form-data"), dbFile);
        MultipartBody.Part fileBody = MultipartBody.Part.createFormData(Constant.FILE_TAG, dbFile.getName(), requestFile);
        MultipartBody.Part md5Body = MultipartBody.Part.createFormData(Constant.MD5_TAG, fileFullMd5);

        Response<JsonRespone> response = null;
        try {
            response = executeCall(ServiceFactory.getSyncService(url).pushReaderData(fileId, fileBody, md5Body));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (response == null || !response.isSuccessful()) {
            String error = "push fail";
            if (response != null) {
                error += "(code:" + response.code() + "message " + response.message() + ")";
            }
            throw new Exception(error);
        }
    }

}
