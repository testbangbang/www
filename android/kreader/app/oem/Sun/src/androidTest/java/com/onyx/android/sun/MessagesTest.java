package com.onyx.android.sun;

import android.test.ApplicationTestCase;

import com.onyx.android.sun.cloud.bean.PracticesRequestBean;
import com.onyx.android.sun.cloud.bean.PracticesResultBean;
import com.onyx.android.sun.requests.HomeworkUnfinishedRequest;
import com.onyx.android.sun.requests.MessagesRequest;
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
        PracticesRequestBean requestBean = new PracticesRequestBean();
        requestBean.studentId = "2";
        requestBean.page = "1";
        requestBean.size = "10";

        final MessagesRequest rq = new MessagesRequest(requestBean);
        SunRequestManager.getInstance().submitRequest(SunApplication.getInstence(), rq, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                PracticesResultBean resultBean = rq.getResultBean();
                assertNotNull(resultBean);
                assertTrue(resultBean.msg.equals("ok"));
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }
}
