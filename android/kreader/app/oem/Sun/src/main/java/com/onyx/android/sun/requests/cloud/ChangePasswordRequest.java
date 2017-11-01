package com.onyx.android.sun.requests.cloud;

import android.util.Log;

import com.onyx.android.sun.cloud.bean.ChangePasswordRequestBean;
import com.onyx.android.sun.cloud.bean.ChangePasswordResultBean;
import com.onyx.android.sun.cloud.service.ContentService;
import com.onyx.android.sun.common.CloudApiContext;
import com.onyx.android.sun.requests.requestTool.BaseCloudRequest;
import com.onyx.android.sun.requests.requestTool.SunRequestManager;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by jackdeng on 2017/10/26.
 */

public class ChangePasswordRequest extends BaseCloudRequest {
    private final static String TAG = ChangePasswordRequest.class.getSimpleName();
    private ChangePasswordRequestBean requestBean;
    private ChangePasswordResultBean resultBean;

    public ChangePasswordRequest(ChangePasswordRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public ChangePasswordResultBean getChangePasswordResultBean() {
        return resultBean;
    }

    @Override
    public void execute(SunRequestManager helper) throws Exception {
        try {
            ContentService service = CloudApiContext.getService(CloudApiContext.BASE_URL);
            Call<ChangePasswordResultBean> call = getCall(service);
            Response<ChangePasswordResultBean> response = call.execute();
            if (response.isSuccessful()) {
                resultBean = response.body();
            }else {
                //The changePassword api is not complete, and the simulation result callback is temporarily used
                resultBean = new ChangePasswordResultBean();
                resultBean.code = CloudApiContext.HttpReusltCode.RESULT_CODE_SUCCESS;
                resultBean.msg = CloudApiContext.HttpReusltCode.RESULT_MESSAGE_SUCCESS;
            }
        } catch (Exception e) {
            Log.i(TAG, e.toString());
            setException(e);
        }
    }

    private Call<ChangePasswordResultBean> getCall(ContentService service) {
        return service.changePassword(requestBean.account,requestBean.oldPassword,requestBean.newPpassword);
    }
}
