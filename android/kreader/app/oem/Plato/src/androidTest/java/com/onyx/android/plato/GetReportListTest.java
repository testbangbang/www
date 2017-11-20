package com.onyx.android.plato;

import android.test.ApplicationTestCase;

import com.onyx.android.plato.cloud.bean.GetReportListBean;
import com.onyx.android.plato.requests.cloud.GetReportListRequest;
import com.onyx.android.plato.requests.requestTool.BaseCallback;
import com.onyx.android.plato.requests.requestTool.BaseRequest;
import com.onyx.android.plato.requests.requestTool.SunRequestManager;

import java.util.concurrent.CountDownLatch;

/**
 * Created by li on 2017/11/20.
 */

public class GetReportListTest extends ApplicationTestCase<SunApplication> {
    public GetReportListTest() {
        super(SunApplication.class);
    }

    public void testGetReportList() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final GetReportListRequest rq = new GetReportListRequest(1,105);
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                GetReportListBean reportList = rq.getReportList();
                assertNotNull(reportList);
                assertNotNull(reportList.data);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }
}
