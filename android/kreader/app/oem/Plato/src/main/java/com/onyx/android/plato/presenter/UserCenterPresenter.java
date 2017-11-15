package com.onyx.android.plato.presenter;

import com.onyx.android.plato.cloud.bean.UserLogoutRequestBean;
import com.onyx.android.plato.cloud.bean.UserLogoutResultBean;
import com.onyx.android.plato.common.CloudApiContext;
import com.onyx.android.plato.data.UserCenterFragmentData;
import com.onyx.android.plato.interfaces.UserLogoutView;
import com.onyx.android.plato.requests.cloud.UserLogoutRequest;
import com.onyx.android.plato.requests.requestTool.BaseCallback;
import com.onyx.android.plato.requests.requestTool.BaseRequest;

/**
 * Created by jackdeng on 2017/10/24.
 */

public class UserCenterPresenter {
    private UserCenterFragmentData userCenterFragmentData;
    private UserLogoutView logoutView;

    public UserCenterPresenter(UserLogoutView logoutView) {
        userCenterFragmentData = new UserCenterFragmentData();
        this.logoutView = logoutView;
    }

    public void logoutAccount(String account) {
        UserLogoutRequestBean requestBean = new UserLogoutRequestBean();
        requestBean.account = account;
        final UserLogoutRequest rq = new UserLogoutRequest(requestBean);
        userCenterFragmentData.userLogOut(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                UserLogoutResultBean resultBean = rq.getLogoutResultBean();
                if (resultBean == null) {
                    logoutView.onLogoutError(e);
                    return;
                }
                if (resultBean.code == CloudApiContext.HttpReusltCode.RESULT_CODE_SUCCESS){
                    logoutView.onLogoutSucced();
                } else {
                    logoutView.onLogoutFailed(resultBean.code,resultBean.msg);
                }

            }
        });
    }
}
