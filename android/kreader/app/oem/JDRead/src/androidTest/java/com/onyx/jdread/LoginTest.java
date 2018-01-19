package com.onyx.jdread;

import android.test.ApplicationTestCase;
import android.util.Log;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.jdread.personal.cloud.entity.jdbean.SaltResultBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.SyncLoginInfoBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.UserInfoBean;
import com.onyx.jdread.personal.request.cloud.RxGetSaltRequest;
import com.onyx.jdread.personal.request.cloud.RxRequestSyncLoginInfo;
import com.onyx.jdread.personal.request.cloud.RxRequestUserInfo;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;

import java.util.concurrent.CountDownLatch;

/**
 * Created by li on 2018/1/11.
 */

public class LoginTest extends ApplicationTestCase<JDReadApplication> {
    public LoginTest() {
        super(JDReadApplication.class);
    }

    public void testSyncLogin() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        String signValue = baseInfo.getSignValue(CloudApiContext.User.SYNC_INFO);
        baseInfo.setSign(signValue);

        final RxRequestSyncLoginInfo rq = new RxRequestSyncLoginInfo();
        rq.setRequestBean(baseInfo);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                SyncLoginInfoBean syncLoginInfoBean = rq.getSyncLoginInfoBean();
                assertNotNull(syncLoginInfoBean);
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                assertNull(throwable);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testGetSalt() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        final RxGetSaltRequest rq = new RxGetSaltRequest(baseInfo);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                SaltResultBean saltResultBean = rq.getSaltResultBean();
                assertNotNull(saltResultBean);
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                assertNull(throwable);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testGetUserInfo() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        baseInfo.removeApp();
        String signValue = baseInfo.getSignValue(CloudApiContext.User.GET_USER_INFO);
        baseInfo.setSign(signValue);
        final RxRequestUserInfo rq = new RxRequestUserInfo();
        rq.setSaltValue("1513304880000");
        rq.setUserInfoRequestBean(baseInfo);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                UserInfoBean userInfoBean = rq.getUserInfoBean();
                assertNotNull(userInfoBean);
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                assertNull(throwable);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }
}
