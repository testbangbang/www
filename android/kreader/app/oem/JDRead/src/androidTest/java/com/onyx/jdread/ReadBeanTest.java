package com.onyx.jdread;

import android.test.ApplicationTestCase;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.personal.cloud.entity.jdbean.ConsumeRecordBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.GetPayQRCodeBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.GetRechargePackageBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.GetRechargeStatusBean;
import com.onyx.jdread.personal.request.cloud.RxGetConsumeRecordRequest;
import com.onyx.jdread.personal.request.cloud.RxGetPayQRCodeRequest;
import com.onyx.jdread.personal.request.cloud.RxGetReadBeanRecordRequest;
import com.onyx.jdread.personal.request.cloud.RxGetRechargeStatusRequest;
import com.onyx.jdread.personal.request.cloud.RxRechargePackageRequest;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by li on 2018/1/25.
 */

public class ReadBeanTest extends ApplicationTestCase<JDReadApplication> {
    public ReadBeanTest() {
        super(JDReadApplication.class);
    }

    public void testRechargePackage() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        baseInfo.removeApp();
        String signValue = baseInfo.getSignValue(CloudApiContext.ReadBean.RECHARGE_PACKAGE);
        baseInfo.setSign(signValue);

        final RxRechargePackageRequest rq = new RxRechargePackageRequest();
        rq.setSaltValue("1513304880000");
        rq.setBaseInfo(baseInfo);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                GetRechargePackageBean resultBean = rq.getResultBean();
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

    public void testGetPayQRCode() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        baseInfo.removeApp();
        Map<String, String> map = new HashMap<>();
        map.put("package_id", "1");
        baseInfo.getRequestParamsMap().putAll(map);
        String signValue = baseInfo.getSignValue(CloudApiContext.ReadBean.RECHARGE);
        baseInfo.setSign(signValue);

        final RxGetPayQRCodeRequest rq = new RxGetPayQRCodeRequest();
        rq.setSaltValue("1513304880000");
        rq.setBaseInfo(baseInfo);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                GetPayQRCodeBean qrCodeBean = rq.getQrCodeBean();
                assertNotNull(qrCodeBean);
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

    public void testGetRechargeStatus() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        Map<String, String> map = new HashMap<>();
        map.put("order_id", "1");
        baseInfo.getRequestParamsMap().putAll(map);
        String signValue = baseInfo.getSignValue(CloudApiContext.ReadBean.RECHARGE_STATUS);
        baseInfo.setSign(signValue);

        final RxGetRechargeStatusRequest rq = new RxGetRechargeStatusRequest();
        rq.setBaseInfo(baseInfo);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                GetRechargeStatusBean rechargeStatusBean = rq.getRechargeStatusBean();
                assertNotNull(rechargeStatusBean);
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

    public void testGetConsumeRecord() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        baseInfo.setPageSize("1", "20");
        String signValue = baseInfo.getSignValue(CloudApiContext.ReadBean.CONSUME_RECORD);
        baseInfo.setSign(signValue);

        final RxGetConsumeRecordRequest rq = new RxGetConsumeRecordRequest();
        rq.setBaseInfo(baseInfo);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ConsumeRecordBean consumeRecordBean = rq.getConsumeRecordBean();
                assertNotNull(consumeRecordBean);
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

    public void testGetReadBeanRecord() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        JDAppBaseInfo baseInfo = new JDAppBaseInfo();
        baseInfo.setPageSize("1", "20");
        String signValue = baseInfo.getSignValue(CloudApiContext.ReadBean.READ_BEAN_RECORD);
        baseInfo.setSign(signValue);

        final RxGetReadBeanRecordRequest rq = new RxGetReadBeanRecordRequest();
        rq.setBaseInfo(baseInfo);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ConsumeRecordBean readBeanRecord = rq.getReadBeanRecord();
                assertNotNull(readBeanRecord);
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
