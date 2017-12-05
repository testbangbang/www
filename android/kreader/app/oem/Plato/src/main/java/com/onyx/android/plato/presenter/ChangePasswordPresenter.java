package com.onyx.android.plato.presenter;

import com.onyx.android.plato.cloud.bean.ChangePasswordRequestBean;
import com.onyx.android.plato.cloud.bean.ChangePasswordResultBean;
import com.onyx.android.plato.cloud.bean.ModifyPasswordBean;
import com.onyx.android.plato.cloud.bean.SubmitPracticeResultBean;
import com.onyx.android.plato.common.CloudApiContext;
import com.onyx.android.plato.data.ChangePasswordFragmentData;
import com.onyx.android.plato.interfaces.ChangePasswordView;
import com.onyx.android.plato.requests.cloud.ModifyPasswordRequest;
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

    public void modifyPassword(String oldPassword, String newPassword) {
        ModifyPasswordBean bean = new ModifyPasswordBean();
        bean.oldPassword = oldPassword;
        bean.newPassword = newPassword;
        final ModifyPasswordRequest rq = new ModifyPasswordRequest(bean);
        changePasswordFragmentData.modifyPassword(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    changePasswordView.onChangePasswordError(e);
                    return;
                }
                SubmitPracticeResultBean resultBean = rq.getResultBean();
                if (resultBean == null) {
                    changePasswordView.onChangePasswordFailed(rq.getErrorBean());
                    return;
                }
                changePasswordView.onChangePasswordSucceed();
            }
        });
    }
}
