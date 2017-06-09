package com.onyx.android.sdk.data.request.cloud;

import android.content.Context;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.model.JsonRespone;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.data.v2.ContentService;
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

public class SaveDocumentDataToCloudRequest extends BaseCloudRequest {

    private String uploadDBPath;
    private Context context;
    private String url;
    private String fileFullMd5;
    private String docId;
    private String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1OTMyMzBhZDY4Njg3MWViMDY1YWUxN2IiLCJyb2xlIjoiYWRtaW4iLCJpYXQiOjE0OTY5NzgxNjEsImV4cCI6MTQ5OTU3MDE2MX0.J5p5ttj-IgFNNI3pdJCNtflCg36AFzbr6_R3XB9PeOo";

    public SaveDocumentDataToCloudRequest(String uploadDBPath,
                                          Context context,
                                          String url,
                                          String fileFullMd5,
                                          String docId) {
        this.uploadDBPath = uploadDBPath;
        this.context = context;
        this.url = url;
        this.fileFullMd5 = fileFullMd5;
        this.docId = docId;
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
        if (StringUtils.isNullOrEmpty(docId)) {
            return;
        }
        updateTokenHeader(parent);
        File dbFile = new File(uploadDBPath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("form-data"), dbFile);
        MultipartBody.Part fileBody = MultipartBody.Part.createFormData(Constant.FILE_TAG, dbFile.getName(), requestFile);
        MultipartBody.Part md5Body = MultipartBody.Part.createFormData(Constant.MD5_TAG, fileFullMd5);
        MultipartBody.Part docIdBody = MultipartBody.Part.createFormData(Constant.DOCID_TAG, docId);

        Response<JsonRespone> response = null;
        try {
            response = executeCall(ServiceFactory.getSyncService(url).pushReaderData(fileBody, md5Body, docIdBody));
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

    private void updateTokenHeader(final CloudManager cloudManager) {
        if (StringUtils.isNotBlank(token)) {
            ServiceFactory.addRetrofitTokenHeader(url,
                    Constant.HEADER_AUTHORIZATION,
                    ContentService.CONTENT_AUTH_PREFIX + token);
        }
    }
}
