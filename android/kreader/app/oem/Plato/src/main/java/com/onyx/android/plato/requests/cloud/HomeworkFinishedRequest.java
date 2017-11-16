package com.onyx.android.plato.requests.cloud;

import android.util.Log;

import com.onyx.android.plato.cloud.bean.HomeworkFinishedResultBean;
import com.onyx.android.plato.cloud.bean.HomeworkRequestBean;
import com.onyx.android.plato.cloud.service.ContentService;
import com.onyx.android.plato.common.CloudApiContext;
import com.onyx.android.plato.requests.requestTool.BaseCloudRequest;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2017/10/10.
 */

public class HomeworkFinishedRequest extends BaseCloudRequest {
    private static final String TAG = HomeworkFinishedRequest.class.getSimpleName();
    private HomeworkRequestBean requestBean;
    private HomeworkFinishedResultBean resultBean;

    public HomeworkFinishedRequest(HomeworkRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public HomeworkFinishedResultBean getResultBean() {
        return resultBean;
    }

    @Override
    public void execute(SunRequestManager helper) throws Exception {
        try {
            ContentService service = CloudApiContext.getService(CloudApiContext.BASE_URL);
            Call<HomeworkFinishedResultBean> call = getCall(service);
            Response<HomeworkFinishedResultBean> response = call.execute();
            if (response.isSuccessful()) {
                resultBean = response.body();
            }
        } catch (Exception e) {
            Log.i(TAG, e.toString());
            setException(e);
        }
    }

    private Call<HomeworkFinishedResultBean> getCall(ContentService service) {
        return service.getHomeworkFinished(requestBean.status, requestBean.studentId,
                requestBean.page, requestBean.size,
                requestBean.course, requestBean.type,
                requestBean.starttime, requestBean.endtime);
    }
}
