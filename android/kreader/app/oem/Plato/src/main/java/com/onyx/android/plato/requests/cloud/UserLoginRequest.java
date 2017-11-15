package com.onyx.android.plato.requests.cloud;

import android.util.Log;

import com.onyx.android.plato.cloud.bean.UserInfoBean;
import com.onyx.android.plato.cloud.bean.UserLoginRequestBean;
import com.onyx.android.plato.cloud.bean.UserLoginResultBean;
import com.onyx.android.plato.cloud.service.ContentService;
import com.onyx.android.plato.common.CloudApiContext;
import com.onyx.android.plato.requests.requestTool.BaseCloudRequest;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by jackdeng on 2017/10/21.
 */

public class UserLoginRequest extends BaseCloudRequest {
    private final static String TAG = UserLoginRequest.class.getSimpleName();
    private UserLoginRequestBean requestBean;
    private UserLoginResultBean resultBean;

    public UserLoginRequest(UserLoginRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public UserLoginResultBean getLoginResultBean() {
        return resultBean;
    }

    @Override
    public void execute(SunRequestManager helper) throws Exception {
        try {
            ContentService service = CloudApiContext.getService(CloudApiContext.BASE_URL);
            Call<UserLoginResultBean> call = getCall(service);
            Response<UserLoginResultBean> response = call.execute();
            if (response.isSuccessful()) {
                resultBean = response.body();
            }else {
                //The login api is not complete, and the simulation result callback is temporarily used
                resultBean = new UserLoginResultBean();
                resultBean.code = CloudApiContext.HttpReusltCode.RESULT_CODE_SUCCESS;
                resultBean.msg = CloudApiContext.HttpReusltCode.RESULT_MESSAGE_SUCCESS;
                UserInfoBean userInfoBean = new UserInfoBean();
                userInfoBean.name = "jack";
                userInfoBean.phoneNumber = "13456781256";
                userInfoBean.account = requestBean.account;
                resultBean.data = userInfoBean;
            }
        } catch (Exception e) {
            Log.i(TAG, e.toString());
            setException(e);
        }
    }

    private Call<UserLoginResultBean> getCall(ContentService service) {
        return service.userLogin(requestBean.account,requestBean.password);
    }
}
