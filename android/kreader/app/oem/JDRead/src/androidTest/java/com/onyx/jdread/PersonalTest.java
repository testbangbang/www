package com.onyx.jdread;

import android.test.ApplicationTestCase;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.personal.cloud.entity.jdbean.GetReadPreferenceBean;
import com.onyx.jdread.personal.request.cloud.RxGetReadPreferenceRequest;
import com.onyx.jdread.personal.request.cloud.RxReadingForVoucherRequest;
import com.onyx.jdread.personal.request.cloud.RxSetReadPreferenceRequest;
import com.onyx.jdread.personal.request.cloud.RxSignForVoucherRequest;
import com.onyx.jdread.personal.request.cloud.RxVerifySignRequest;
import com.onyx.jdread.shop.cloud.entity.BaseShopRequestBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by li on 2018/1/22.
 */

public class PersonalTest extends ApplicationTestCase<JDReadApplication> {
    public PersonalTest() {
        super(JDReadApplication.class);
    }

    public void testSetReadPreference() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        BaseShopRequestBean bean = new BaseShopRequestBean();
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        String signValue = baseInfo.getSignValue(CloudApiContext.User.READ_PREFERENCE);
        baseInfo.setSign(signValue);
        bean.setBaseInfo(baseInfo);

        List<Integer> list = new ArrayList<>();
        list.add(5347);
        list.add(5684);
        String s = JSON.toJSONString(list);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), s);
        bean.setBody(requestBody);
        final RxSetReadPreferenceRequest rq = new RxSetReadPreferenceRequest();
        rq.setRequestBean(bean);

        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testGetReadPreference() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        BaseShopRequestBean bean = new BaseShopRequestBean();
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        String signValue = baseInfo.getSignValue(CloudApiContext.User.READ_PREFERENCE);
        baseInfo.setSign(signValue);
        bean.setBaseInfo(baseInfo);

        final RxGetReadPreferenceRequest rq = new RxGetReadPreferenceRequest();
        rq.setRequestBean(bean);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                GetReadPreferenceBean resultBean = rq.getResultBean();
                assertNotNull(resultBean);
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

    //Verify that the user is checking in.
    public void testVerifyCheckIn() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        String signValue = baseInfo.getSignValue(CloudApiContext.User.SIGN_CHECK);
        baseInfo.setSign(signValue);

        final RxVerifySignRequest rq = new RxVerifySignRequest();
        rq.setBaseInfo(baseInfo);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
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

    public void testSignForVoucher() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        String signValue = baseInfo.getSignValue(CloudApiContext.User.SIGN_CHECK);
        baseInfo.setSign(signValue);

        final RxSignForVoucherRequest rq = new RxSignForVoucherRequest();
        rq.setBaseInfo(baseInfo);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
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

    public void testReadingForVoucher() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        String signValue = baseInfo.getSignValue(CloudApiContext.User.READING_VOUCHER);
        baseInfo.setSign(signValue);

        final RxReadingForVoucherRequest rq = new RxReadingForVoucherRequest();
        rq.setBaseInfo(baseInfo);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
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
