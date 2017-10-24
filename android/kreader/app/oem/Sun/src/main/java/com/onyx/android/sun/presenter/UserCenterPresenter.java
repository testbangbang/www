package com.onyx.android.sun.presenter;

import com.onyx.android.sun.cloud.bean.UserLogoutRequestBean;
import com.onyx.android.sun.cloud.bean.UserLogoutResultBean;
import com.onyx.android.sun.data.UserCenterActivityData;
import com.onyx.android.sun.interfaces.UserLogoutView;
import com.onyx.android.sun.requests.cloud.UserLogoutRequest;
import com.onyx.android.sun.requests.requestTool.BaseCallback;
import com.onyx.android.sun.requests.requestTool.BaseRequest;

/**
 * Created by jackdeng on 2017/10/24.
 */

public class UserCenterPresenter {
    private UserCenterActivityData userCenterActivityData;
    private UserLogoutView logoutView;

    public UserCenterPresenter(UserLogoutView logoutView) {
        userCenterActivityData = new UserCenterActivityData();
        this.logoutView = logoutView;
    }

    public void logoutAccount(String account) {
        UserLogoutRequestBean requestBean = new UserLogoutRequestBean();
        requestBean.account = account;
        final UserLogoutRequest rq = new UserLogoutRequest(requestBean);
        userCenterActivityData.userLogOut(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                UserLogoutResultBean resultBean = rq.getLogoutResultBean();
                if (resultBean == null) {
                    logoutView.onLogoutError(e);
                    return;
                }
                if (resultBean.code == 0){
                    logoutView.onLogoutSucced();
                } else {
                    logoutView.onLogoutFailed(resultBean.code,resultBean.msg);
                }

            }
        });
    }
}
