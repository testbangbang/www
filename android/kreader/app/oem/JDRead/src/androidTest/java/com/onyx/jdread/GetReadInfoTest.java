package com.onyx.jdread;

import android.test.ApplicationTestCase;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.main.common.ClientUtils;
import com.onyx.jdread.personal.cloud.entity.GetReadInfoRequestBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.ReadOverInfoBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.ReadTotalInfoBean;
import com.onyx.jdread.personal.request.cloud.RxGetReadOverRequest;
import com.onyx.jdread.personal.request.cloud.RxGetReadTotalRequest;

import java.util.concurrent.CountDownLatch;

/**
 * Created by li on 2018/1/2.
 */

public class GetReadInfoTest extends ApplicationTestCase<JDReadApplication> {
    public GetReadInfoTest() {
        super(JDReadApplication.class);
    }

    public void testGetReadTotalInfo() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        GetReadInfoRequestBean requestBean = new GetReadInfoRequestBean();
        requestBean.setAppBaseInfo(JDReadApplication.getInstance().getAppBaseInfo());
        requestBean.setUserName(ClientUtils.getWJLoginHelper().getPin());

        final RxGetReadTotalRequest rq = new RxGetReadTotalRequest();
        rq.setRequestBean(requestBean);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ReadTotalInfoBean readTotalInfoBean = rq.getReadTotalInfoBean();
                assertNotNull(readTotalInfoBean);
                assertEquals(0, readTotalInfoBean.getCode());
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testGetReadOverInfo() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        GetReadInfoRequestBean requestBean = new GetReadInfoRequestBean();
        requestBean.setAppBaseInfo(JDReadApplication.getInstance().getAppBaseInfo());
        requestBean.setUserName(ClientUtils.getWJLoginHelper().getPin());

        final RxGetReadOverRequest rq = new RxGetReadOverRequest();
        rq.setRequestBean(requestBean);
        rq.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ReadOverInfoBean readOverInfoBean = rq.getReadOverInfoBean();
                assertNotNull(readOverInfoBean);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }
}
