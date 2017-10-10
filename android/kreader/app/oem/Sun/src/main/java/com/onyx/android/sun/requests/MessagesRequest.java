package com.onyx.android.sun.requests;

import com.onyx.android.sun.cloud.bean.PracticesRequestBean;
import com.onyx.android.sun.cloud.bean.PracticesResultBean;
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
    private PracticesRequestBean requestBean;
    private PracticesResultBean resultBean;

    public void setRequestBean(PracticesRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public PracticesResultBean getResultBean() {
        return resultBean;
    }

    public MessagesRequest(PracticesRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    @Override
    public void execute(SunRequestManager helper) throws Exception {
        executeCloudRequest();
    }

    private void executeCloudRequest() {
        try {
            ContentService service = CloudApiContext.getService(CloudApiContext.BASE_URL);
            Call<PracticesResultBean> call = getCall(service);
            Response<PracticesResultBean> response = call.execute();
            if (response.isSuccessful()) {
                resultBean = response.body();
            }
        } catch (Exception e) {
            setException(e);
        }
    }

    private Call<PracticesResultBean> getCall(ContentService service) {
        return service.getMessage(requestBean.studentId,
                requestBean.page, requestBean.size);
    }
}
