package com.onyx.android.plato.requests.cloud;


import com.onyx.android.plato.cloud.bean.UploadBean;
import com.onyx.android.plato.cloud.service.ContentService;
import com.onyx.android.plato.common.CloudApiContext;
import com.onyx.android.plato.requests.requestTool.BaseCloudRequest;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2017/11/24.
 */

public class RequestUploadFile extends BaseCloudRequest {
    private MultipartBody.Part part;
    private UploadBean uploadBean;

    public RequestUploadFile(File file) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
    }

    public UploadBean getBean() {
        return uploadBean;
    }

    @Override
    public void execute(SunRequestManager helper) throws Exception {
        ContentService service = CloudApiContext.getService(CloudApiContext.UPLOAD_URL);
        Call<UploadBean> call = getCall(service);
        Response<UploadBean> response = call.execute();
        if (response.isSuccessful()) {
            uploadBean = response.body();
        }
    }

    private Call<UploadBean> getCall(ContentService service) {
        Call<UploadBean> call = service.getUploadKey(part);
        return call;
    }
}
