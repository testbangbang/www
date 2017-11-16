package com.onyx.android.plato.requests.cloud;

import android.util.Log;

import com.onyx.android.plato.cloud.bean.HomeworkRequestBean;
import com.onyx.android.plato.cloud.bean.HomeworkUnfinishedResultBean;
import com.onyx.android.plato.cloud.service.ContentService;
import com.onyx.android.plato.common.CloudApiContext;
import com.onyx.android.plato.requests.requestTool.BaseCloudRequest;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by jackdeng on 2017/10/20.
 */

public class GetNewMessageRequest extends BaseCloudRequest {
    private final static String TAG = GetNewMessageRequest.class.getSimpleName();
    private HomeworkRequestBean requestBean;
    private HomeworkUnfinishedResultBean resultBean;

    public GetNewMessageRequest(HomeworkRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public HomeworkUnfinishedResultBean getHomeworkUnfinishedResultBean() {
        return resultBean;
    }

    @Override
    public void execute(SunRequestManager helper) throws Exception {
        try {
            ContentService service = CloudApiContext.getService(CloudApiContext.BASE_URL);
            Call<HomeworkUnfinishedResultBean> call = getCall(service);
            Response<HomeworkUnfinishedResultBean> response = call.execute();
            if (response.isSuccessful()) {
                resultBean = response.body();
            }
        } catch (Exception e) {
            Log.i(TAG, e.toString());
            setException(e);
        }
    }

    private Call<HomeworkUnfinishedResultBean> getCall(ContentService service) {
        return service.getMessage(requestBean.studentId,requestBean.page,requestBean.size);
    }
}
