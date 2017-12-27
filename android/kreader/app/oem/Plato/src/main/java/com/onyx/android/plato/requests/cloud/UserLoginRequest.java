package com.onyx.android.plato.requests.cloud;

import com.onyx.android.plato.cloud.bean.LoginRequestBean;
import com.onyx.android.plato.cloud.bean.UserLoginResultBean;
import com.onyx.android.plato.cloud.service.ContentService;
import com.onyx.android.plato.common.CloudApiContext;
import com.onyx.android.plato.event.ExceptionEvent;
import com.onyx.android.plato.requests.requestTool.BaseCloudRequest;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by jackdeng on 2017/10/21.
 */

public class UserLoginRequest extends BaseCloudRequest {
    private final static String TAG = UserLoginRequest.class.getSimpleName();
    private LoginRequestBean requestBean;
    private UserLoginResultBean resultBean;
    private String error;

    public UserLoginRequest(LoginRequestBean requestBean) {
        this.requestBean = requestBean;
    }

    public UserLoginResultBean getLoginResultBean() {
        return resultBean;
    }

    public String getError() {
        return error;
    }

    @Override
    public void execute(SunRequestManager helper) {
        try {
            ContentService service = CloudApiContext.getService(CloudApiContext.BASE_URL);
            Call<UserLoginResultBean> call = getCall(service);
            Response<UserLoginResultBean> response = call.clone().execute();
            if (response.isSuccessful()) {
                resultBean = response.body();
            } else {
                error = response.errorBody().string();
            }
        } catch (Exception e) {
            EventBus.getDefault().post(new ExceptionEvent(e.toString()));
            setException(e);
        }
    }

    private Call<UserLoginResultBean> getCall(ContentService service) {
        return service.userLogin(requestBean);
    }
}
