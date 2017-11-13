package com.onyx.android.sun;

import android.test.ApplicationTestCase;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sun.cloud.bean.ChangePasswordRequestBean;
import com.onyx.android.sun.cloud.bean.ChangePasswordResultBean;
import com.onyx.android.sun.cloud.bean.PracticeAnswerBean;
import com.onyx.android.sun.cloud.bean.SubmitPracticeRequestBean;
import com.onyx.android.sun.cloud.bean.SubmitPracticeResultBean;
import com.onyx.android.sun.cloud.bean.UserLoginRequestBean;
import com.onyx.android.sun.cloud.bean.UserLoginResultBean;
import com.onyx.android.sun.cloud.bean.UserLogoutRequestBean;
import com.onyx.android.sun.cloud.bean.UserLogoutResultBean;
import com.onyx.android.sun.data.ChangePasswordFragmentData;
import com.onyx.android.sun.data.UserCenterFragmentData;
import com.onyx.android.sun.data.UserLoginActivityData;
import com.onyx.android.sun.requests.cloud.ChangePasswordRequest;
import com.onyx.android.sun.requests.cloud.SubmitPracticeRequest;
import com.onyx.android.sun.requests.cloud.UserLoginRequest;
import com.onyx.android.sun.requests.cloud.UserLogoutRequest;
import com.onyx.android.sun.requests.requestTool.BaseCallback;
import com.onyx.android.sun.requests.requestTool.BaseRequest;
import com.onyx.android.sun.requests.requestTool.SunRequestManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import okhttp3.MediaType;
import okhttp3.RequestBody;

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
        UserLoginRequestBean requestBean = new UserLoginRequestBean();
        requestBean.account = "123456";
        requestBean.password = "321";
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

    public void testChangePassword() throws Exception {

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ChangePasswordFragmentData changePasswordFragmentData = new ChangePasswordFragmentData();
        ChangePasswordRequestBean requestBean = new ChangePasswordRequestBean();
        requestBean.account = "123456";
        requestBean.newPpassword = "abcd";
        requestBean.finalPassword = "abcd";
        final ChangePasswordRequest rq = new ChangePasswordRequest(requestBean);

        changePasswordFragmentData.changePassword(rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                ChangePasswordResultBean resultBean = rq.getChangePasswordResultBean();
                assertEquals(0,resultBean.code);
                assertNotNull(resultBean);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }
}
