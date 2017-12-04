package com.onyx.android.plato;

import android.test.ApplicationTestCase;

import com.onyx.android.plato.cloud.bean.ChangePasswordRequestBean;
import com.onyx.android.plato.cloud.bean.ChangePasswordResultBean;
import com.onyx.android.plato.cloud.bean.LoginRequestBean;
import com.onyx.android.plato.cloud.bean.ModifyPasswordBean;
import com.onyx.android.plato.cloud.bean.SubmitPracticeResultBean;
import com.onyx.android.plato.cloud.bean.UserCenterBean;
import com.onyx.android.plato.cloud.bean.UserLoginResultBean;
import com.onyx.android.plato.cloud.bean.UserLogoutRequestBean;
import com.onyx.android.plato.cloud.bean.UserLogoutResultBean;
import com.onyx.android.plato.data.ChangePasswordFragmentData;
import com.onyx.android.plato.data.UserCenterFragmentData;
import com.onyx.android.plato.data.UserLoginActivityData;
import com.onyx.android.plato.requests.cloud.GetUserInfoRequest;
import com.onyx.android.plato.requests.cloud.ModifyPasswordRequest;
import com.onyx.android.plato.requests.cloud.UserLoginRequest;
import com.onyx.android.plato.requests.cloud.UserLogoutRequest;
import com.onyx.android.plato.requests.requestTool.BaseCallback;
import com.onyx.android.plato.requests.requestTool.BaseRequest;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;

import java.util.concurrent.CountDownLatch;

/**
 * Created by jackdeng on 17-10-20.
 */

public class UserTest extends ApplicationTestCase<SunApplication> {
    public UserTest() {
        super(SunApplication.class);
    }

    public void testUserLogin() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        UserLoginActivityData userLoginData = new UserLoginActivityData();
        LoginRequestBean requestBean = new LoginRequestBean();
        requestBean.username = "1713000053";
        requestBean.password = "E10ADC3949BA59ABBE56E057F20F883E";
        final UserLoginRequest rq = new UserLoginRequest(requestBean);

        userLoginData.userLogin(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                UserLoginResultBean resultBean = rq.getLoginResultBean();
                assertEquals(0,resultBean.code);
                assertNotNull(resultBean);
                assertNotNull(resultBean.data);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testUserLogout() throws Exception {

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        UserCenterFragmentData userCenterData = new UserCenterFragmentData();
        UserLogoutRequestBean requestBean = new UserLogoutRequestBean();
        requestBean.account = "123456";
        final UserLogoutRequest rq = new UserLogoutRequest(requestBean);

        userCenterData.userLogOut(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                UserLogoutResultBean resultBean = rq.getLogoutResultBean();
                assertEquals(0,resultBean.code);
                assertNotNull(resultBean);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testGetUserInfo() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final GetUserInfoRequest rq = new GetUserInfoRequest();
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                UserCenterBean userCenterBean = rq.getUserCenterBean();
                assertNotNull(userCenterBean);
                assertNotNull(userCenterBean.data);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testModifyPassword() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ModifyPasswordBean bean = new ModifyPasswordBean();
        bean.oldPassword = "96E79218965EB72C92A549DD5A330112";
        bean.newPassword = "E10ADC3949BA59ABBE56E057F20F883E";

        final ModifyPasswordRequest rq = new ModifyPasswordRequest(bean);
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                SubmitPracticeResultBean resultBean = rq.getResultBean();
                assertNotNull(resultBean);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }
}
