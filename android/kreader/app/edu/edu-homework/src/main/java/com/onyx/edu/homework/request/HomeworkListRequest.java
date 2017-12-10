package com.onyx.edu.homework.request;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.HomeworkRequestModel;
import com.onyx.android.sdk.data.model.Question;
import com.onyx.android.sdk.data.request.cloud.BaseCloudRequest;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.List;

import retrofit2.Response;

/**
 * Created by lxm on 2017/12/5.
 */

public class HomeworkListRequest extends BaseCloudRequest {

    private String libraryId;
    private HomeworkRequestModel homeworkRequestModel;

    public HomeworkListRequest(String libraryId) {
        this.libraryId = libraryId;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        Response<HomeworkRequestModel> response = executeCall(ServiceFactory.getHomeworkService(parent.getCloudConf().getApiBase()).getHomeworks(libraryId));
        homeworkRequestModel = response.body();
    }

    public HomeworkRequestModel getHomeworkRequestModel() {
        return homeworkRequestModel;
    }
}
