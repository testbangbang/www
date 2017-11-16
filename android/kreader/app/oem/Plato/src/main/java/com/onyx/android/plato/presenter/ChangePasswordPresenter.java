package com.onyx.android.plato.presenter;

import com.onyx.android.plato.cloud.bean.ChangePasswordRequestBean;
import com.onyx.android.plato.cloud.bean.ChangePasswordResultBean;
import com.onyx.android.plato.common.CloudApiContext;
import com.onyx.android.plato.data.ChangePasswordFragmentData;
import com.onyx.android.plato.interfaces.ChangePasswordView;
import com.onyx.android.plato.requests.cloud.ChangePasswordRequest;
import com.onyx.android.plato.requests.requestTool.BaseCallback;
import com.onyx.android.plato.requests.requestTool.BaseRequest;

/**
 * Created by jackdeng on 2017/10/26.
 */

public class ChangePasswordPresenter {
    private ChangePasswordFragmentData changePasswordFragmentData;
    private ChangePasswordView changePasswordView;

    public ChangePasswordPresenter(ChangePasswordView changePasswordView) {
        changePasswordFragmentData = new ChangePasswordFragmentData();
        this.changePasswordView = changePasswordView;
    }

    public void changePassword(String account,String newPassword) {
        ChangePasswordRequestBean requestBean = new ChangePasswordRequestBean();
        requestBean.account = account;
        requestBean.finalPassword = newPassword;
        final ChangePasswordRequest rq = new ChangePasswordRequest(requestBean);
        changePasswordFragmentData.changePassword(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                ChangePasswordResultBean resultBean = rq.getChangePasswordResultBean();
                if (resultBean == null) {
                    changePasswordView.onChangePasswordError(e);
                    return;
                }
                if (resultBean.code == CloudApiContext.HttpReusltCode.RESULT_CODE_SUCCESS){
                    changePasswordView.onChangePasswordSucced();
                } else {
                    changePasswordView.onChangePasswordFailed(resultBean.code,resultBean.msg);
                }
            }
        });
    }
}
