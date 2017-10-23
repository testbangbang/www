package com.onyx.android.sun;

import android.test.ApplicationTestCase;

import com.onyx.android.sun.cloud.bean.HomeworkRequestBean;
import com.onyx.android.sun.cloud.bean.HomeworkUnfinishedResultBean;
import com.onyx.android.sun.requests.cloud.MessagesRequest;
import com.onyx.android.sun.requests.requestTool.BaseCallback;
import com.onyx.android.sun.requests.requestTool.BaseRequest;
import com.onyx.android.sun.requests.requestTool.SunRequestManager;

import java.util.concurrent.CountDownLatch;

/**
 * Created by hehai on 17-10-10.
 */

public class MessagesTest extends ApplicationTestCase<SunApplication> {
    public MessagesTest() {
        super(SunApplication.class);
    }

    public void testMessages() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        HomeworkRequestBean requestBean = new HomeworkRequestBean();
        requestBean.studentId = "2";
        requestBean.page = "1";
        requestBean.size = "10";

        final MessagesRequest rq = new MessagesRequest(requestBean);
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstance(), rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                HomeworkUnfinishedResultBean resultBean = rq.getResultBean();
                assertNotNull(resultBean);
                assertNotNull(resultBean.data);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }
}
