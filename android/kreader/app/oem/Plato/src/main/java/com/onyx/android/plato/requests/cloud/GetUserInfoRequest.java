package com.onyx.android.plato.requests.cloud;

import com.onyx.android.plato.cloud.bean.UserCenterBean;
import com.onyx.android.plato.cloud.service.ContentService;
import com.onyx.android.plato.common.CloudApiContext;
import com.onyx.android.plato.requests.requestTool.BaseCloudRequest;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by li on 2017/12/4.
 */

public class GetUserInfoRequest extends BaseCloudRequest {
    private UserCenterBean userCenterBean;

    public UserCenterBean getUserCenterBean() {
        return userCenterBean;
    }

    @Override
    public void execute(SunRequestManager helper) throws Exception {
        ContentService service = CloudApiContext.getService(CloudApiContext.BASE_URL);
        Call<UserCenterBean> call = getCall(service);
        Response<UserCenterBean> response = call.execute();
        if (response.isSuccessful()) {
            userCenterBean = response.body();
        }
    }

    private Call<UserCenterBean> getCall(ContentService service) {
        Call<UserCenterBean> call = service.getUserInfo();
        return call;
    }
}
