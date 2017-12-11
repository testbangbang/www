package com.onyx.edu.homework.request;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.HomeworkRequestModel;
import com.onyx.android.sdk.data.model.HomeworkSubmitBody;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by lxm on 2017/12/5.
 */

public class HomeworkSubmitRequest extends BaseCloudRequest {

    private String libraryId;
    private HomeworkSubmitBody body;
    private Response<ResponseBody> response;

    public HomeworkSubmitRequest(String libraryId, HomeworkSubmitBody body) {
        this.libraryId = libraryId;
        this.body = body;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        response = executeCall(ServiceFactory.getHomeworkService(parent.getCloudConf().getApiBase()).submitAnswers(libraryId, body));
    }

    public boolean isSuccess() {
        return response != null && response.isSuccessful();
    }
}
