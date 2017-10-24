package com.onyx.android.sun.requests.cloud;

import android.util.Log;

import com.onyx.android.sun.cloud.bean.UserLogoutRequestBean;
import com.onyx.android.sun.cloud.bean.UserLogoutResultBean;
import com.onyx.android.sun.cloud.service.ContentService;
import com.onyx.android.sun.common.CloudApiContext;
import com.onyx.android.sun.requests.requestTool.BaseCloudRequest;
import com.onyx.android.sun.requests.requestTool.SunRequestManager;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by jackdeng on 2017/10/24.
 */

public class UserLogoutRequest extends BaseCloudRequest {
    private final static String TAG = UserLogoutRequest.class.getSimpleName();
    private UserLogoutRequestBean requestBean;
    private UserLogoutResultBean resultBean;

    public UserLogoutRequest(UserLogoutRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public UserLogoutResultBean getLogoutResultBean() {
        return resultBean;
    }

    @Override
    public void execute(SunRequestManager helper) throws Exception {
        try {
            ContentService service = CloudApiContext.getService(CloudApiContext.BASE_URL);
            Call<UserLogoutResultBean> call = getCall(service);
            Response<UserLogoutResultBean> response = call.execute();
            if (response.isSuccessful()) {
                resultBean = response.body();
            }else {
                //The logout api is not complete, and the simulation result callback is temporarily used
                resultBean = new UserLogoutResultBean();
                resultBean.code = 0;
                resultBean.msg = "ok";
            }
        } catch (Exception e) {
            Log.i(TAG, e.toString());
            setException(e);
        }
    }

    private Call<UserLogoutResultBean> getCall(ContentService service) {
        return service.userLogout(requestBean.account);
    }
}
