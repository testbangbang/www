package com.onyx.android.sun.requests.cloud;

import com.onyx.android.sun.cloud.bean.HomeworkRequestBean;
import com.onyx.android.sun.cloud.bean.HomeworkUnfinishedResultBean;
import com.onyx.android.sun.cloud.service.ContentService;
import com.onyx.android.sun.common.CloudApiContext;
import com.onyx.android.sun.requests.requestTool.BaseCloudRequest;
import com.onyx.android.sun.requests.requestTool.SunRequestManager;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by hehai on 17-10-9.
 */

public class MessagesRequest extends BaseCloudRequest {
    private HomeworkRequestBean requestBean;
    private HomeworkUnfinishedResultBean resultBean;

    public void setRequestBean(HomeworkRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public HomeworkUnfinishedResultBean getResultBean() {
        return resultBean;
    }

    public MessagesRequest(HomeworkRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    @Override
    public void execute(SunRequestManager helper) throws Exception {
        executeCloudRequest();
    }

    private void executeCloudRequest() {
        try {
            ContentService service = CloudApiContext.getService(CloudApiContext.BASE_URL);
            Call<HomeworkUnfinishedResultBean> call = getCall(service);
            Response<HomeworkUnfinishedResultBean> response = call.execute();
            if (response.isSuccessful()) {
                resultBean = response.body();
            }
        } catch (Exception e) {
            setException(e);
        }
    }

    private Call<HomeworkUnfinishedResultBean> getCall(ContentService service) {
        return service.getMessage(requestBean.studentId,
                requestBean.page, requestBean.size);
    }
}
